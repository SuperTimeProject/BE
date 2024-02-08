package org.supercoding.supertime.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.PostImageRepository;
import org.supercoding.supertime.repository.PostRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.board.CreatePostRequestDto;
import org.supercoding.supertime.web.dto.board.EditPostRequestDto;
import org.supercoding.supertime.web.dto.board.getBoardPost.GetBoardPostDetailDto;
import org.supercoding.supertime.web.dto.board.getBoardPost.GetBoardPostResponseDto;
import org.supercoding.supertime.web.dto.board.getPostDetail.GetPostDetailResponseDto;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailDto;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailImageDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.board.PostImageEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageUploadService imageUploadService;
    private final PostImageRepository postImageRepository;

    @Transactional
    public CommonResponseDto createPost(Long boardCid, User user, CreatePostRequestDto createPostInfo, List<MultipartFile> images) {
        // TODO
        // - 게시판 유무 확인
        // - 게시판 작성 권한 확인
        // - 게시물 생성
        BoardEntity targetBoard = boardRepository.findById(boardCid).orElseThrow(()-> new NotFoundException("게시판이 존재하지 않습니다."));
        // 시큐리티 적용시 토큰기반으로 검색하는 로직으로 수정
        UserEntity author = userRepository.findByUserId(user.getUsername()).orElseThrow(()-> new NotFoundException("일치하는 유저가 존재하지 않습니다."));
        // TODO (희망사항) 권한 정보에 대한 칼럼을 추가하여 유저가 포함되어있는지 확인하는 구문 구현

        PostEntity newPost = PostEntity.builder()
                .boardEntity(targetBoard)
                .userEntity(author)
                .postTitle(createPostInfo.getPostTitle())
                .postContent(createPostInfo.getPostContent())
                .build();

        if(images != null){
            List<PostImageEntity> uploadImages = imageUploadService.uploadImages(images, "post");
            newPost.setPostImages(uploadImages);
            log.info("[CREATE_POST] 상품에 이미지가 추가되었습니다.");
        }

        postRepository.save(newPost);

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시물 작성이 성공적으로 이루어졌습니다.")
                .build();
    }

    @Transactional
    public CommonResponseDto editPost(Long postCid, User user, EditPostRequestDto editPostInfo, List<MultipartFile> images) {
        PostEntity targetPost = postRepository.findById(postCid).orElseThrow(()->new NotFoundException("수정하려는 게시물이 존재하지 않습니다."));

        if(targetPost.getUserEntity().getUserId() != user.getUsername()){
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        List<PostImageEntity> imageList = targetPost.getPostImages();

        if(editPostInfo.getPostTitle() != null){
            targetPost.setPostTitle(editPostInfo.getPostTitle());
        }

        if(editPostInfo.getPostContent() != null){
            targetPost.setPostContent(editPostInfo.getPostContent());
        }

        List<PostImageEntity> deletedImages = new ArrayList<>();

        if(!imageList.isEmpty()){
            for(PostImageEntity image: imageList){
                if(editPostInfo.getDeleteImageList().contains(image.getPostImageCid())){
                    deletedImages.add(image);
                    imageUploadService.deleteImage(image.getPostImageFilePath());
                    postImageRepository.deleteById(image.getPostImageCid());
                }
            }

            imageList.removeAll(deletedImages);
        }

        if(images != null){
            List<PostImageEntity> uploadImages = imageUploadService.uploadImages(images, "post");
            imageList.addAll(uploadImages);
            targetPost.setPostImages(imageList);
        }

        postRepository.save(targetPost);

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시물 수정이 성공적으로 이루어졌습니다.")
                .build();
    }

    @Transactional
    public CommonResponseDto deletePost(Long postCid, User user) {
        PostEntity targetPost = postRepository.findById(postCid)
                .orElseThrow(() -> new NotFoundException("삭제하려는 게시물이 존재하지 않습니다."));

        if(targetPost.getUserEntity().getUserId() != user.getUsername()){
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        List<PostImageEntity> postImages = targetPost.getPostImages();

        if (!postImages.isEmpty()) {
            for (PostImageEntity imageEntity : postImages) {
                imageUploadService.deleteImage(imageEntity.getPostImageFilePath());
                log.info("[DELETE] " + imageEntity.getPostImageFileName() + " 이미지가 삭제되었습니다");
            }
        }

        postRepository.delete(targetPost);

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시물을 성공적으로 삭제하였습니다.")
                .build();
    }

    public GetBoardPostResponseDto getBoardPost(Long boardCid) {
        List<PostEntity> postList = postRepository.findAllByBoardEntity_BoardCid(boardCid);
        List<GetBoardPostDetailDto> postListDto = new ArrayList<>();

        if(postList.isEmpty()){
            throw new NoSuchElementException("게시판에 게시글이 없습니다.");
        }

        for(PostEntity post: postList){
            GetBoardPostDetailDto postDetail = GetBoardPostDetailDto.builder()
                    .author(post.getUserEntity().getUserNickname())
                    .postCid(post.getPostCid())
                    .postTitle(post.getPostTitle())
                    .postView(post.getPostView())
                    .createdAt(toSimpleDate(post.getCreatedAt()))
                    .build();

            postListDto.add(postDetail);
        }

        return GetBoardPostResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시판에 포함된 게시물을 불러왔습니다.")
                .postList(postListDto)
                .build();
    }

    @Transactional
    public GetPostDetailResponseDto getPostDetail(Long postCid, HttpServletRequest req, HttpServletResponse res) {
        PostEntity targetPost = postRepository.findById(postCid)
                .orElseThrow(() -> new NotFoundException("조회하려는 게시물이 존재하지 않습니다."));
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

        return GetPostDetailResponseDto.builder()
                .code(200)
                .success(true)
                .message("게시물을 성공적으로 불러왔습니다.")
                .postInfo(postDetailDto)
                .build();

    }

    public String toSimpleDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }
}
