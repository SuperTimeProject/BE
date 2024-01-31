package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.service.BoardService;
import org.supercoding.supertime.web.dto.board.CreatePostRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/Board")
@RequiredArgsConstructor
@Tag(name = "게시판 관련 API", description = "게시판 관련")
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시물 생성", description = "게시물을 생성하는 api입니다.")
    @PostMapping("/create/{boardCid}")
    public ResponseEntity<CommonResponseDto> createPost(@PathVariable Long boardCid, @RequestBody CreatePostRequestDto createPostInfo){
        log.info("[BOARD] 게시물 생성 요청이 들어왔습니다.");
        CommonResponseDto createPostResult = boardService.createPost(boardCid, createPostInfo);
        log.info("[BOARD] 게시물 생성 결과 = " + createPostResult);

        return ResponseEntity.ok(createPostResult);
    }
}
