package org.supercoding.supertime.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.board.PostEntity;
import org.supercoding.supertime.web.entity.board.PostImageEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
  
    List<PostEntity> findAllByBoardEntity_BoardCid(Long boardCid);
    List<PostEntity> findAllByUserEntity_UserCid(Long userCid);
    Page<PostEntity> findAllByBoardEntity_BoardCid(Long boardCid, Pageable pageable);

    Page<PostEntity> findAllByBoardEntity_BoardCidAndUserEntity_UserCid( Long boardCid, Long userCid, Pageable pageable);
}
