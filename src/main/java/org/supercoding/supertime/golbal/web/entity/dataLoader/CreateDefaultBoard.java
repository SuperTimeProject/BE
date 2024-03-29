package org.supercoding.supertime.golbal.web.entity.dataLoader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.board.web.entity.BoardEntity;

@RequiredArgsConstructor
@Component
@Slf4j
public class CreateDefaultBoard implements CommandLineRunner {
    private final BoardRepository repository;

    @Override
    public void run(String... args) throws Exception {
        createDefaultBoard();
    }

    private void createDefaultBoard(){
        String[] defaultBoardName = {"전체 게시판", "커뮤니티 게시판","FE 스터디","BE 스터디"};

        for(String boardName : defaultBoardName){
           if(!repository.existsByBoardName(boardName)){
               BoardEntity newBoard = BoardEntity.builder()
                       .boardName(boardName)
                       .build();
               repository.save(newBoard);
               log.info("[DEFAULT_DATA] 기본 게시판이 생성되었습니다. 추가된 게시판 = " + boardName);
           }
           else{
               log.info("[DEFAULT_DATA] 이미 게시판이 존재하여 생성하지 않았습니다. 존제하는 게시판 = " + boardName);
           }
        }
    }
}
