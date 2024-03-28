package org.supercoding.supertime.board.service;

import com.nimbusds.jose.util.Pair;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.board.repository.PostImageRepository;
import org.supercoding.supertime.board.repository.PostRepository;
import org.supercoding.supertime.board.util.PostValidation;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.aws.service.ImageUploadService;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.board.web.dto.CreatePostRequestDto;
import org.supercoding.supertime.board.web.dto.EditPostRequestDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.BoardInfoDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.GetBoardPostDetailDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.GetBoardPostResponseDto;
import org.supercoding.supertime.board.web.dto.getPostDetail.GetPostDetailResponseDto;
import org.supercoding.supertime.board.web.dto.getPostDetail.PostDetailDto;
import org.supercoding.supertime.board.web.dto.getPostDetail.PostDetailImageDto;
import org.supercoding.supertime.board.web.dto.getUserPost.GetUserPostDto;
import org.supercoding.supertime.board.web.dto.getUserPost.GetUserPostResponseDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.board.web.entity.PostEntity;
import org.supercoding.supertime.board.web.entity.PostImageEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final PostRepository postRepository;
    private final ImageUploadService imageUploadService;
    private final PostImageRepository postImageRepository;
    private final PostValidation postValidation;

    private static final int SECONDS_IN_A_DAY = 60 * 60 * 24;

    /**
     * 기능 - 게시물 생성
     * @param boardCid
     * @param user
     * @param createPostInfo
     * @param images
     *
     * @return void
     */
    @Transactional
    public void createPost(Long boardCid, User user, CreatePostRequestDto createPostInfo, List<MultipartFile> images) {

        Pair<UserEntity, BoardEntity> userAndBoardPair = postValidation.validateUserWriteAccessToBoard(user, boardCid);
        UserEntity author = userAndBoardPair.getLeft();
        BoardEntity targetBoard = userAndBoardPair.getRight();

        PostEntity newPost = PostEntity.create(targetBoard, author, createPostInfo);

        if(images != null){
            List<PostImageEntity> uploadImages = imageUploadService.uploadImages(images, "post");
            newPost.setPostImages(uploadImages);
            log.info("[CREATE_POST] 상품에 이미지가 추가되었습니다.");
        }

        postRepository.save(newPost);
    }

    /**
     * 기능 - 게시물 수정
     *
     * @param postCid
     * @param user
     * @param editPostInfo
     * @param images
     *
     * @return void
     */
    @Transactional
    public void editPost(Long postCid, User user, EditPostRequestDto editPostInfo, List<MultipartFile> images) {
        PostEntity targetPost = postValidation.validatePostExistence(postCid);
        postValidation.validatePostEditPermission(targetPost, user);

        updatePost(targetPost, editPostInfo);

        List<PostImageEntity> deletedImages = deleteImages(targetPost.getPostImages(), editPostInfo.getDeleteImageList());

        List<PostImageEntity> uploadedImages = uploadImages(images);
        targetPost.getPostImages().addAll(uploadedImages);

        postRepository.save(targetPost);

        deleteImagesFromS3(deletedImages);
    }

    private void updatePost(PostEntity targetPost, EditPostRequestDto editPostInfo) {
        if (editPostInfo.getPostTitle() != null) {
            targetPost.setPostTitle(editPostInfo.getPostTitle());
        }
        if (editPostInfo.getPostContent() != null) {
            targetPost.setPostContent(editPostInfo.getPostContent());
        }
    }

    private List<PostImageEntity> deleteImages(List<PostImageEntity> imageList, List<Long> deleteImageList) {
        List<PostImageEntity> deletedImages = new ArrayList<>();
        if (!imageList.isEmpty() && deleteImageList != null && !deleteImageList.isEmpty()) {
            for (PostImageEntity image : imageList) {
                if (deleteImageList.contains(image.getPostImageCid())) {
                    deletedImages.add(image);
                    postImageRepository.deleteById(image.getPostImageCid());
                }
            }
            imageList.removeAll(deletedImages);
        }
        return deletedImages;
    }

    private List<PostImageEntity> uploadImages(List<MultipartFile> images) {
        List<PostImageEntity> uploadImages = new ArrayList<>();
        if (images != null) {
            uploadImages = imageUploadService.uploadImages(images, "post");
        }
        return uploadImages;
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteImagesFromS3(List<PostImageEntity> images) {
        for (PostImageEntity image : images) {
            imageUploadService.deleteImage(image.getPostImageFilePath());
        }
    }

    /**
     * 기능 - 게시물 삭제
     *
     * @param postCid
     * @param user
     *
     * @return void
     */
    @Transactional
    public void deletePost(Long postCid, User user) {
        PostEntity targetPost = postValidation.validatePostExistence(postCid);

        postValidation.validatePostEditPermission(targetPost, user);

        List<PostImageEntity> postImages = targetPost.getPostImages();

        postRepository.delete(targetPost);

        if (!postImages.isEmpty()) {
           deleteImagesFromS3(postImages);
        }
    }

    /**
     * 기능 - 게시판 조회
     *
     * @param user
     * @param boardCid
     * @param page
     *
     * @return postList, boardInfo
     */
    @Transactional(readOnly = true)
    public Pair<List<GetBoardPostDetailDto>, BoardInfoDto> getBoardPost(User user, Long boardCid, int page) {
        UserEntity userEntity = postValidation.validateUserExistence(user.getUsername());

        postValidation.validateGetBoardPermission(userEntity, boardCid);

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<PostEntity> postList = postRepository.findAllByBoardEntity_BoardCid(boardCid, pageable);
        List<GetBoardPostDetailDto> postListDto = new ArrayList<>();

        postValidation.validatePostListIsEmpty(postList);

        for(PostEntity post: postList){
            String simpleDate = toSimpleDate(post.getCreatedAt());
            postListDto.add(GetBoardPostDetailDto.from(post, simpleDate));
        }

        return Pair.of(postListDto, BoardInfoDto.from(postList, page));
    }

    /**
     * 기능 - 게시물 조회
     *
     * @param postCid
     * @param req
     * @param res
     *
     * @return PostDetailDto
     */
    @Transactional(readOnly = true)
    public PostDetailDto getPostDetail(Long postCid, HttpServletRequest req, HttpServletResponse res) {
        PostEntity targetPost = postValidation.validatePostExistence(postCid);

        updatePostView(targetPost, req, res);

        return createPostDetailDto(targetPost);

    }

    private void updatePostView(PostEntity targetPost, HttpServletRequest req, HttpServletResponse res) {
        Cookie oldCookie = findCookieByNamm("postView", req.getCookies());
        if (oldCookie != null && !oldCookie.getValue().contains("[" + targetPost.getPostCid() + "]")) {
            targetPost.updatePostView();
            oldCookie.setValue(oldCookie.getValue() + "_[" + targetPost.getPostCid() + "]");
            res.addCookie(updateCookie(oldCookie));
        } else if (oldCookie == null) {
            targetPost.updatePostView();
            Cookie newCookie = createNewCookie("postView", "[" + targetPost.getPostCid() + "]");
            res.addCookie(newCookie);
        }
        postRepository.save(targetPost);
    }

    private Cookie findCookieByNamm(String name, Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private Cookie updateCookie(Cookie cookie) {
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 쿠키 시간
        return cookie;
    }

    private Cookie createNewCookie(String name, String value) {
        Cookie newCookie = new Cookie(name, value);
        newCookie.setPath("/");
        newCookie.setMaxAge(SECONDS_IN_A_DAY);
        return newCookie;
    }

    private PostDetailDto createPostDetailDto(PostEntity targetPost) {
        List<PostDetailImageDto> imageList = new ArrayList<>();
        if (!targetPost.getPostImages().isEmpty()) {
            for (PostImageEntity image : targetPost.getPostImages()) {
                PostDetailImageDto postImage = PostDetailImageDto.from(image);
                imageList.add(postImage);
            }
        }

        final String simpleDate = toSimpleDate(targetPost.getCreatedAt());
        return PostDetailDto.from(targetPost, imageList, simpleDate);
    }

    /**
     * 기능 - 유저 게시물 조회
     *
     * @param user
     * @param boardCid
     * @param page
     *
     * @return List<GetUserPostDto>
     */
    @Transactional(readOnly = true)
    public Pair<List<GetUserPostDto>, BoardInfoDto> getUserPost(User user, Long boardCid, int page) {

        UserEntity userEntity = postValidation.validateUserExistence(user.getUsername());

        postValidation.validateUserAccessToBoard(userEntity, boardCid);

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<PostEntity> userPostList = postRepository.findAllByBoardEntity_BoardCidAndUserEntity_UserCid( boardCid, userEntity.getUserCid(), pageable);

        return Pair.of(createUserPostDtoList(userPostList), BoardInfoDto.from(userPostList, page));
    }

    private List<GetUserPostDto> createUserPostDtoList(Page<PostEntity> userPostList) {
        if(userPostList.isEmpty()) {
            throw new CustomNoSuchElementException("리스트가 비어있습니다.");
        }

        List<GetUserPostDto> userPostDtoList = new ArrayList<>();
        for(PostEntity post : userPostList){
            final String simpleDate = toSimpleDate(post.getCreatedAt());
            userPostDtoList.add( GetUserPostDto.from(post, simpleDate));
        }

        return userPostDtoList;
    }


    private String toSimpleDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

}
