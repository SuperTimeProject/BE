package org.supercoding.supertime.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.comment.repository.CommentRepository;
import org.supercoding.supertime.comment.web.dto.request.CreateCommentReqDto;
import org.supercoding.supertime.comment.web.dto.response.GetCommentDetailDto;
import org.supercoding.supertime.comment.web.dto.response.GetCommentResDto;
import org.supercoding.supertime.comment.web.entity.PostCommentEntity;
import org.supercoding.supertime.repository.PostRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.web.advice.CustomNotFoundException;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public CommonResponseDto createComment(User user, CreateCommentReqDto commentInfo) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 없습니다."));

        PostEntity postEntity = postRepository.findById(commentInfo.getPostCid())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 게시글이 없습니다."));

        PostCommentEntity comment = PostCommentEntity.builder()
                .user(userEntity)
                .postEntity(postEntity)
                .comment(commentInfo.getContent())
                .build();

        commentRepository.save(comment);

        return CommonResponseDto.successResponse("댓글이 성공적으로 추가되었습니다.");
    }

    public GetCommentResDto getComment(Long postCid, int page) {
        PostEntity postEntity = postRepository.findById(postCid)
                .orElseThrow(() -> new CustomNotFoundException("일치하는 게시글이 없습니다."));

        Pageable pageable = PageRequest.of(page-1, 10);
        Page<PostCommentEntity> commentList = commentRepository.findAllByPostEntity(postEntity, pageable);
        List<GetCommentDetailDto> commentDtoList = new ArrayList<>();

        if(commentList.isEmpty()){
            throw new CustomNoSuchElementException("댓글이 비어있습니다.");
        }


        for(PostCommentEntity comment: commentList){
            GetCommentDetailDto postComment = GetCommentDetailDto.from(comment);
            commentDtoList.add(postComment);
        }

        return GetCommentResDto.success(commentDtoList);
    }
}
