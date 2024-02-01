package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.board.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    boolean existsByBoardName(String boardName);
}