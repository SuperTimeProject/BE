package org.supercoding.supertime.comment.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.board.repository.PostRepository;
import org.supercoding.supertime.board.web.entity.PostEntity;
import org.supercoding.supertime.comment.repository.CommentRepository;
import org.supercoding.supertime.comment.web.entity.PostCommentEntity;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@RequiredArgsConstructor
@Component
public class CommentValidation {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public UserEntity validateExistUser(String username) {
        return userRepository.findByUserId(username)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 없습니다."));
    }

    public PostEntity validateExistPost(Long postCid) {
        return postRepository.findById(postCid)
                .orElseThrow(() -> new CustomNotFoundException("일치하는 게시글이 없습니다."));
    }

    public Page<PostCommentEntity> validateExistComment(PostEntity postEntity, Pageable pageable) {
        Page<PostCommentEntity> commentList = commentRepository.findAllByPostEntity(postEntity, pageable);

        if(commentList.isEmpty()) {
            throw new CustomNoSuchElementException("댓글이 비어있습니다.");
        }

        return commentList;
    }
}
