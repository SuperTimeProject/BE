package org.supercoding.supertime.comment.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.comment.service.CommentService;
import org.supercoding.supertime.comment.web.dto.request.CreateCommentReqDto;
import org.supercoding.supertime.comment.web.dto.response.GetCommentDetailDto;
import org.supercoding.supertime.comment.web.dto.response.GetCommentResDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

import java.nio.file.Path;

@RestController
@Slf4j
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(tags = {"게시판 CRUD API"}, summary = "댓글 추가", description = "댓글을 작성하는 api입니다.")
    @PostMapping("/create")
    public ResponseEntity<CommonResponseDto> createComment(
            @AuthenticationPrincipal User user,
            @RequestBody CreateCommentReqDto commentInfo
            ){
        log.info("[COMMENT] 댓글 작성 요청이 들어왔습니다.");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(user, commentInfo));
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "댓글 조회", description = "댓글을 조회하는 api입니다.")
    @GetMapping("/getComment/{postCid}/{page}")
    public ResponseEntity<GetCommentResDto> getComment(
            @PathVariable Long postCid,
            @PathVariable int page
    ){
        log.info("[COMMENT] 댓글 조회 요청이 들어왔습니다.");

        return ResponseEntity.ok(commentService.getComment(postCid, page));
    }
}
