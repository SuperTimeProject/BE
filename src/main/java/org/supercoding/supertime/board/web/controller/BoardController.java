package org.supercoding.supertime.board.web.controller;

import com.nimbusds.jose.util.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.board.service.BoardService;
import org.supercoding.supertime.board.web.dto.CreatePostRequestDto;
import org.supercoding.supertime.board.web.dto.EditPostRequestDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.BoardInfoDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.GetBoardPostDetailDto;
import org.supercoding.supertime.board.web.dto.getBoardPost.GetBoardPostResponseDto;
import org.supercoding.supertime.board.web.dto.getPostDetail.GetPostDetailResponseDto;
import org.supercoding.supertime.board.web.dto.getPostDetail.PostDetailDto;
import org.supercoding.supertime.board.web.dto.getUserPost.GetUserPostDto;
import org.supercoding.supertime.board.web.dto.getUserPost.GetUserPostResponseDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 생성", description = "스웨거에서 테스트를 진행할 떄에는 productInfo도 json파일로 생성해서 테스트 진행해 주셔야합니다.")
    @PostMapping(value = "/posts/{boardCid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> createPost(
            @PathVariable Long boardCid,
            @AuthenticationPrincipal User user,
            @RequestPart(name = "postInfo") @Parameter(schema = @Schema(type = "string", format = "binary")) CreatePostRequestDto createPostInfo,
            @RequestPart(name = "postImage", required = false) List<MultipartFile> postImages
    ){
        log.debug("[BOARD] 게시물 생성 요청이 들어왔습니다.");
        boardService.createPost(boardCid,user, createPostInfo, postImages);
        log.debug("[BOARD] 성공적으로 게시물을 생성하였습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponseDto.successResponse("게시물 작성이 성공적으로 이루어졌습니다."));
    }

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 수정", description = "게시물을 수정하는 api입니다.")
    @PutMapping(value = "/posts/{postCid}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> editPost(
            @PathVariable Long postCid,
            @AuthenticationPrincipal User user,
            @RequestPart(name = "editPostInfo") @Parameter(schema = @Schema(type = "string", format = "binary")) EditPostRequestDto editPostInfo,
            @RequestPart(name = "postImage",required = false) List<MultipartFile> postImages
                                                      ){
        log.debug("[BOARD] 게시물 수정 요청이 들어왔습니다.");
        boardService.editPost(postCid, user, editPostInfo, postImages);
        log.debug("[BOARD] 게시물이 성공적으로 수정되었습니다." );

        return ResponseEntity.ok(CommonResponseDto.successResponse("게시물이 성공적으로 수정되었습니다."));
    }

    @Operation(tags = {"게시판 CRUD API"}, summary = "게시물 삭제", description = "게시물을 삭제하는 api입니다.")
    @DeleteMapping("/posts/{postCid}")
    public ResponseEntity<CommonResponseDto> deletePost(
            @PathVariable Long postCid,
            @AuthenticationPrincipal User user
    ){
        log.debug("[BOARD] 게시물 삭제 요청이 들어왔습니다.");
        boardService.deletePost(postCid, user);
        log.debug("[BOARD] 게시물을 성공적으로 삭제하였습니다");

        return ResponseEntity.ok(CommonResponseDto.successResponse("게시물을 성공적으로 삭제하였습니다."));
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "게시판 조회", description = "각 게시판의 게시물을 모두 불러오는 api입니다.")
    @GetMapping("/boards/{boardCid}/{page}")
    public ResponseEntity<GetBoardPostResponseDto> getBoardPosts(
            @AuthenticationPrincipal User user,
            @PathVariable Long boardCid,
            @PathVariable int page
    ){
        log.debug("[BOARD] 게시판 조회 요청이 들어왔습니다.");
        Pair<List<GetBoardPostDetailDto>, BoardInfoDto> postListAndBoardInfoPair = boardService.getBoardPost(user, boardCid, page);
        log.debug("[BOARD] 게시판을 성공적으로조회했습니다.");

        return ResponseEntity.ok(GetBoardPostResponseDto.successResponse("게시판에 포함된 게시물을 불러왔습니다.", postListAndBoardInfoPair.getLeft(), postListAndBoardInfoPair.getRight()));
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "게시물 조회", description = "게시물의 세부 내용을 불러오는 api입니다.")
    @GetMapping("/posts/{postCid}")
    public ResponseEntity<GetPostDetailResponseDto> getPostDetail(@PathVariable Long postCid, HttpServletRequest req, HttpServletResponse res){
        log.debug("[POST] 게시물 조회 요청이 들어왔습니다.");
       PostDetailDto postDetail = boardService.getPostDetail(postCid, req, res);
        log.debug("[POST] 게시물을 성곡적으로 조회했습니다.");

        return ResponseEntity.ok(GetPostDetailResponseDto.successResponse("성공적으로 게시물을 불러왔습니다.",postDetail));
    }

    @Operation(tags = {"게시판 조회 API"}, summary = "유저 게시물 조회", description = "유저가 작성한 글을 불러오는 api입니다.")
    @GetMapping("/posts/user-posts/{boardCid}/{page}")
    public ResponseEntity<GetUserPostResponseDto> getUserPost(
            @AuthenticationPrincipal User user,
            @PathVariable Long boardCid,
            @PathVariable int page
    ){
        log.debug("[GET_USER_POST] 유저 게시물 조회 요청이 들어왔습니다.");
        Pair<List<GetUserPostDto>, BoardInfoDto> userPostListAndBoardInfoPair = boardService.getUserPost(user, boardCid, page);
        log.debug("[GET_USER_POST] 유저 게시물을 성공적으로 불러왔습니다.");

        return ResponseEntity.ok(GetUserPostResponseDto.success("성공적으로 유저 게시물을 불러왔습니다.", userPostListAndBoardInfoPair.getLeft(), userPostListAndBoardInfoPair.getRight()));
    }
}
