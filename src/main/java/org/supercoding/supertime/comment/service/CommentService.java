package org.supercoding.supertime.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.comment.repository.CommentRepository;
import org.supercoding.supertime.comment.util.CommentValidation;
import org.supercoding.supertime.comment.web.dto.request.CreateCommentReqDto;
import org.supercoding.supertime.comment.web.dto.response.GetCommentDetailDto;
import org.supercoding.supertime.comment.web.entity.PostCommentEntity;
import org.supercoding.supertime.board.web.entity.PostEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentValidation commentValidation;

    /**
     * 기능 - 댓글 추가
     * @param user
     * @param commentInfo
     */
    @Transactional
    public void createComment(User user, CreateCommentReqDto commentInfo) {
        UserEntity userEntity = commentValidation.validateExistUserByUsername(user.getUsername());

        createCommentEntity(userEntity.getUserCid(), commentInfo);
    }

    private void createCommentEntity(Long userCid, CreateCommentReqDto commentInfo) {
        PostCommentEntity comment = PostCommentEntity.from(userCid, commentInfo);

        commentRepository.save(comment);
    }

    /**
     * 기능 - 게시물 댓글 조회
     * @param postCid
     * @param page
     * @return List<GetCommentDetailDto>
     */
    @Transactional(readOnly = true)
    public List<GetCommentDetailDto> getPostComment(Long postCid, int page) {
        PostEntity postEntity = commentValidation.validateExistPost(postCid);

        Page<PostCommentEntity> commentList = getCommentList(postEntity, page);

        return commentListToDto(commentList);

    }

    private Page<PostCommentEntity> getCommentList(PostEntity postEntity, int page) {
        Pageable pageable = PageRequest.of(page-1, 10);

        return commentValidation.validateExistComment(postEntity, pageable);
    }

    private List<GetCommentDetailDto> commentListToDto(Page<PostCommentEntity> commentList) {

        List<GetCommentDetailDto> commentDtoList = new ArrayList<>();

        for (PostCommentEntity comment : commentList) {
            UserEntity user = commentValidation.validateExistUser(comment.getUserCid());

            GetCommentDetailDto postComment = GetCommentDetailDto.from(comment, user);
            commentDtoList.add(postComment);
        }

        return commentDtoList;
    }
}
