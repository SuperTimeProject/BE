package org.supercoding.supertime.admin.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.admin.service.VerificationService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/user/verification")
@RequiredArgsConstructor
@Tag(name = "인증 관련 API")
public class VerificationController {
    private final VerificationService verificationService;

    @Operation(summary = "인증 신청", description = "인증 신청하는 api입니다.")
    @PostMapping(value = "/verification/apply" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> apply(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "userProfileImage")MultipartFile image
    ){
        log.info("[VERIFY] 인증 신청 요청이 들어왔습니다.");
        CommonResponseDto applyResult = verificationService.apply(user,image);
        log.info("[VERIFY] 인증 신청 요청 결과 = " + applyResult);
        return ResponseEntity.ok().body(applyResult);
    }

    @Operation(summary = "인증 재신청", description = "인증 신청하는 api입니다.")
    @PutMapping(value = "/verification/reapply" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> reapply(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "userProfileImage")MultipartFile image
    ){
        log.info("[VERIFY] 인증 신청 요청이 들어왔습니다.");
        CommonResponseDto reapplyResult = verificationService.reapply(user,image);
        log.info("[VERIFY] 인증 신청 요청 결과 = " + reapplyResult);
        return ResponseEntity.ok().body(reapplyResult);
    }
}
