package org.supercoding.supertime.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.comment.web.entity.PostCommentEntity;
import org.supercoding.supertime.web.entity.board.PostEntity;

@Repository
public interface CommentRepository extends JpaRepository<PostCommentEntity, Long> {

    Page<PostCommentEntity> findAllByPostEntity(PostEntity postEntity, Pageable pageable);
}
