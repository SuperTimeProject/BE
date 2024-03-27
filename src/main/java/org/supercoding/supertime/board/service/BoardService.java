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
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final PostImageRepository postImageRepository;
    private final PostValidation postValidation;

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

        Pair<UserEntity, BoardEntity> userAndBoardPair = postValidation.validateUserAccessToBoard(user, boardCid);
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

    @Transactional
    public GetPostDetailResponseDto getPostDetail(Long postCid, HttpServletRequest req, HttpServletResponse res) {
        PostEntity targetPost = postRepository.findById(postCid)
                .orElseThrow(() -> new CustomNotFoundException("조회하려는 게시물이 존재하지 않습니다."));
        // 조회수 추가 로직
        Cookie oldCookie = null;
        Cookie[] cookies = req.getCookies();
        if(cookies != null){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals("postView")){
                    oldCookie = cookie;
                }
            }
        }
        if(oldCookie != null){
            if(!oldCookie.getValue().contains("["+ postCid +"]")){
            targetPost.updatePostView();
            oldCookie.setValue(oldCookie.getValue() + "_[" + postCid + "]");
            oldCookie.setPath("/");
            oldCookie.setMaxAge(60*60*24);
            res.addCookie(oldCookie);
            }
        } else {
            targetPost.updatePostView();
            Cookie newCookie = new Cookie("postView", "[" + postCid + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24); 								// 쿠키 시간
            res.addCookie(newCookie);
        }
        postRepository.save(targetPost);

        List<PostDetailImageDto> imageList = new ArrayList<>();

        if(!targetPost.getPostImages().isEmpty()){
            for(PostImageEntity image: targetPost.getPostImages()){
                PostDetailImageDto postImage = PostDetailImageDto.builder()
                        .postImageCid(image.getPostImageCid())
                        .postImageFileName(image.getPostImageFileName())
                        .postImageFilePath(image.getPostImageFilePath())
                        .build();

                imageList.add(postImage);
            }
        }

        PostDetailDto postDetailDto = PostDetailDto.builder()
                .postCid(targetPost.getPostCid())
                .author(targetPost.getUserEntity().getUserNickname()) // 게시판에서는 닉네임을 사용
                .postTitle(targetPost.getPostTitle())
                .postContent(targetPost.getPostContent())
                .imageList(imageList)
                .postView(targetPost.getPostView())
                .createdAt(toSimpleDate(targetPost.getCreatedAt()))
                .build();


        return GetPostDetailResponseDto.successResponse("게시물을 성공적으로 불러왔습니다.", postDetailDto);

    }

    public GetUserPostResponseDto getUserPost(User user, Long boardCid, int page) {
        // TODO
        // 유저가 해당 게시판에 포함되어있는지 확인
        // 해당 보드에 유저가 작성한 글 불러오기

        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("유저 정보가 없습니다."));

        BoardEntity boardEntity = boardRepository.findById(boardCid)
                .orElseThrow(() -> new CustomNotFoundException("게시판이 존재하지 않습니다."));

        if(userEntity.getBoardList().stream()
                .map(BoardEntity::getBoardCid)
                .noneMatch(cid -> cid.equals(boardEntity.getBoardCid()))
        ) {
            throw new CustomAccessDeniedException("게시물 조회 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<PostEntity> userPostList = postRepository.findAllByBoardEntity_BoardCidAndUserEntity_UserCid( boardCid, userEntity.getUserCid(), pageable);

        BoardInfoDto boardInfo = BoardInfoDto.builder()
                .page(page)
                .totalElements(userPostList.getTotalElements())
                .totalPages(userPostList.getTotalPages())
                .build();

        if(userPostList.isEmpty()) {
            throw new CustomNoSuchElementException("리스트가 비어있습니다.");
        }

        List<GetUserPostDto> userPostDtoList = new ArrayList<>();
        for(PostEntity post : userPostList){
            GetUserPostDto userPost = GetUserPostDto.builder()
                    .postCid(post.getPostCid())
                    .postTitle(post.getPostTitle())
                    .createdAt(toSimpleDate(post.getCreatedAt()))
                    .build();
            userPostDtoList.add(userPost);
        }

        return GetUserPostResponseDto.success("성공적으로 유저 게시물을 불러왔습니다.", userPostDtoList, boardInfo);
    }


    public String toSimpleDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

}
