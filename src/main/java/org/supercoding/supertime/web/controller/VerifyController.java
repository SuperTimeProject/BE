package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.service.VerifyService;
import org.supercoding.supertime.web.dto.board.CreatePostRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/verify")
@RequiredArgsConstructor
@Builder
@Tag(name = "마이페이지 API")
public class VerifyController {
    private final VerifyService verifyService;

    @Operation(summary = "인증 사진 업로드", description = "슈퍼코딩 수강중인 캡쳐 이미지를 업로드하는 api입니다.")
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> requestAuthentication(@RequestPart(name = "certificationImage") MultipartFile certificationImage){
        log.info("[BOARD] 게시물 생성 요청이 들어왔습니다.");
        CommonResponseDto createPostResult = verifyService.requestAuthentication(certificationImage);
        log.info("[BOARD] 게시물 생성 결과 = " + createPostResult);

        return ResponseEntity.status(HttpStatus.CREATED).body(createPostResult);
    }

}
