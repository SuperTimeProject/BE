package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.service.AdminService;
import org.supercoding.supertime.web.dto.admin.GetPendingUserDto;
import org.supercoding.supertime.web.dto.admin.UpdateUserInfoRequestDto;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.inquiry.GetUnclosedInquiryResponseDto;
import org.supercoding.supertime.web.entity.enums.InquiryClosed;
import org.supercoding.supertime.web.entity.enums.Valified;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 관련 API")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "회원 인증상태 별 조회", description = "회원인증 인증상태에 따른 대기내역을 조회하는 api입니다.")
    @GetMapping("/pendingUser")
    public ResponseEntity<GetPendingUserDto> getUserByValified(
            @RequestParam String valified,
            @PathVariable int page
    ){
        log.info("[ADMIN] 회원인증 인증상태 별 조회 요청이 들어왔습니다.");
        GetPendingUserDto getPendingResult = adminService.getUserByValified(valified,page);
        log.info("[ADMIN] 인증 결과 = " + getPendingResult);
        return ResponseEntity.ok().body(getPendingResult);
    }

    @Operation(summary = "회원 인증 관리", description = "회원 인증상태를 변경하는 api입니다.")
    @PutMapping("/verification")
    public ResponseEntity<CommonResponseDto> verification(
            @RequestParam String userId,
            @RequestParam String verificationState
    ){
        log.info("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto verifiResult = adminService.verification(userId, verificationState);
        log.info("[ADMIN] 인증 결과 = " + verifiResult);
        return ResponseEntity.ok().body(verifiResult);
    }

    @Operation(summary = "회원 정보 수정", description = "회원의 모든 정보를 수정 할 수 있는 api입니다.")
    @PutMapping("/update/userInfo")
    public ResponseEntity<CommonResponseDto> updateUserInfo(
            @RequestPart(name="userInfo")
            @Parameter(schema = @Schema(type = "string", format = "binary"))
            UpdateUserInfoRequestDto updateUserInfoRequestDto
            ){
        log.info("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto updateUserInfoResult = adminService.updateUserInfo(updateUserInfoRequestDto);
        log.info("[ADMIN] 인증 결과 = " + updateUserInfoResult);
        return ResponseEntity.ok().body(updateUserInfoResult);
    }


    @Operation(summary = "문의 조회하기", description = "문의기록을 조회하는 api입니다.")
    @GetMapping("/inquiry/get/{page}")
    public ResponseEntity<GetUnclosedInquiryResponseDto> getUnclosedInquiry(
            @PathVariable int page
    ){
        log.info("[ADMIN] 문의 조회 요청이 들어왔습니다.");
        GetUnclosedInquiryResponseDto getInquiryResult = adminService.getUnclosedInquiry(page);
        log.info("[ADMIN] 문의 조회 결과 = " + getInquiryResult);
        return ResponseEntity.ok().body(getInquiryResult);
    }

    @Operation(summary = "문의 답변하기", description = "문의에 대한 답변을 하는 api입니다.")
    @PutMapping("/inquiry/answer/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> answerInquiry(
           @PathVariable Long inquiryCid,
           @RequestParam String inquiryContent
    ){
        log.info("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        CommonResponseDto answerResult = adminService.answerInquiry(inquiryCid,inquiryContent);
        log.info("[ADMIN] 답변 결과 = " + answerResult);
        return ResponseEntity.ok().body(answerResult);
    }

    @Operation(summary = "문의 삭제하기", description = "문의를 삭제하는 api입니다.")
    @DeleteMapping("/inquiry/delete/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> deleteInquiry(
            @PathVariable Long inquiryCid
    ){
        log.info("[ADMIN] 문의 삭제 요청이 들어왔습니다.");
        CommonResponseDto deleteResult = adminService.deleteInquiry(inquiryCid);
        log.info("[ADMIN] 답변 결과 = " + deleteResult);
        return ResponseEntity.ok().body(deleteResult);
    }
}
