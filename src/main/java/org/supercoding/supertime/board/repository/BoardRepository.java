package org.supercoding.supertime.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.board.web.entity.BoardEntity;

import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    boolean existsByBoardName(String boardName);

    Optional<BoardEntity> findByBoardName(String boardName);
}
