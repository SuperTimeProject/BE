package org.supercoding.supertime.user.web.controller;

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
import org.supercoding.supertime.user.service.VerificationService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/user/verification")
@RequiredArgsConstructor
@Tag(name = "인증 관련 API")
public class VerificationController {
    private final VerificationService verificationService;

    @Operation(summary = "인증 신청", description = "인증 신청하는 api입니다.")
    @PostMapping(value = "/apply" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> apply(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "userProfileImage")MultipartFile image
    ){
        log.debug("[VERIFY] 인증 신청 요청이 들어왔습니다.");
        verificationService.certificationRequest(user, image);
        log.debug("[VERIFY] 인증 신청 요청이 성공적으로 이루어졌습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("인증 신청 성공"));
    }

    @Operation(summary = "인증 재신청", description = "인증 신청하는 api입니다.")
    @PutMapping(value = "/reapply" ,consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> reapply(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "userProfileImage")MultipartFile image
    ){
        log.debug("[VERIFY] 인증 신청 요청이 들어왔습니다.");
        verificationService.certificationResubmission(user, image);
        log.debug("[VERIFY] 인증 신청 재요청이 성공적으로 이루어졌습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("인증 재신청 성공"));
    }
}