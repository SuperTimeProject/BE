package org.supercoding.supertime.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.board.web.entity.PostImageEntity;

@Repository
public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
}
