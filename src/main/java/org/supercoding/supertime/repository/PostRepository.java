package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.board.PostEntity;
@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
