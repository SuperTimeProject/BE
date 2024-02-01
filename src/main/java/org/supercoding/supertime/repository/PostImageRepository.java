package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.board.PostImageEntity;

@Repository
public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
}
