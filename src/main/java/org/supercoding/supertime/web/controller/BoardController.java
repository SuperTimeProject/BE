package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.service.BoardService;
import org.supercoding.supertime.web.dto.board.CreatePostRequestDto;
import org.supercoding.supertime.web.dto.board.EditPostRequestDto;
import org.supercoding.supertime.web.dto.board.getBoardPost.GetBoardPostResponseDto;
import org.supercoding.supertime.web.dto.board.getPostDetail.GetPostDetailResponseDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/Board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 생성", description = "스웨거에서 테스트를 진행할 떄에는 productInfo도 json파일로 생성해서 테스트 진행해 주셔야합니다.")
    @PostMapping(value = "/create/{boardCid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> createPost(
            @PathVariable Long boardCid,
            @RequestPart(name = "postInfo") @Parameter(schema = @Schema(type = "string", format = "binary")) CreatePostRequestDto createPostInfo,
            @RequestPart(name = "postImage", required = false) List<MultipartFile> postImages
    ){
        log.info("[BOARD] 게시물 생성 요청이 들어왔습니다.");
        CommonResponseDto createPostResult = boardService.createPost(boardCid, createPostInfo, postImages);
        log.info("[BOARD] 게시물 생성 결과 = " + createPostResult);

        return ResponseEntity.ok(createPostResult);
    }

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 수정", description = "게시물을 수정하는 api입니다.")
    @PutMapping("/edit/{postCid}")
    public ResponseEntity<CommonResponseDto> editPost(@PathVariable Long postCid, @RequestBody EditPostRequestDto editPostInfo){
        log.info("[BOARD] 게시물 수정 요청이 들어왔습니다.");
        CommonResponseDto editPostResult = boardService.editPost(postCid, editPostInfo);
        log.info("[BOARD] 게시물 수정 결과 = " + editPostResult);

        return ResponseEntity.ok(editPostResult);
    }

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 삭제", description = "게시물을 삭제하는 api입니다.")
    @DeleteMapping("/delete/{postCid}")
    public ResponseEntity<CommonResponseDto> deletePost(@PathVariable Long postCid){
        log.info("[BOARD] 게시물 삭제 요청이 들어왔습니다.");
        CommonResponseDto deletePostResult = boardService.deletePost(postCid);
        log.info("[BOARD] 게시물 삭제 결과 = " + deletePostResult);

        return ResponseEntity.ok(deletePostResult);
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "게시판 조회", description = "각 게시판의 게시물을 모두 불러오는 api입니다.")
    @GetMapping("/getBoard/{boardCid}")
    public ResponseEntity<GetBoardPostResponseDto> getBoardPosts(@PathVariable Long boardCid){
        log.info("[BOARD] 게시판 조회 요청이 들어왔습니다.");
        GetBoardPostResponseDto getBoardPostsResult = boardService.getBoardPost(boardCid);
        log.info("[BOARD] 게시판 조회 요청 결과 = "+ getBoardPostsResult);

        return ResponseEntity.ok(getBoardPostsResult);
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "게시물 조회", description = "게시물의 세부 내용을 불러오는 api입니다.")
    @GetMapping("/getPost/{postCid}")
    public ResponseEntity<GetPostDetailResponseDto> getPostDetail(@PathVariable Long postCid){
        log.info("[POST] 게시물 조회 요청이 들어왔습니다.");
        GetPostDetailResponseDto getPostDetailResult = boardService.getPostDetail(postCid);
        log.info("[POST] 게시물 조회 요청 결과 = "+ getPostDetailResult);

        return ResponseEntity.ok(getPostDetailResult);
    }
}
