package org.supercoding.supertime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.board.PostImageEntity;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByBoardEntity_BoardCid(Long boardCid, Pageable pageable);

    Page<PostEntity> findAllByBoardEntity_BoardCidAndUserEntity_UserCid(Long userCid, Long boardCid, Pageable pageable);
}
