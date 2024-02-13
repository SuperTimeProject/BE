package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.service.AdminService;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.inquiry.GetUnclosedInquiryResponseDto;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 관련 API")
public class AdminController {

    private AdminService adminService;

    @Operation(summary = "회원 인증 관리", description = "회원 인증상태를 변경하는 api입니다.")
    @PutMapping("/verification")
    public ResponseEntity<CommonResponseDto> verification(String userName){
        log.info("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto verifiResult = adminService.verification(userName);
        log.info("[ADMIN] 인증 결과 = " + verifiResult);
        return ResponseEntity.ok().body(verifiResult);
    }

    @Operation(summary = "문의 답변하기", description = "문의에 대한 답변을 하는 api입니다.")
    @GetMapping("/inquiry/answer/{inquiryCid}")
    public ResponseEntity<GetUnclosedInquiryResponseDto> getUnclosedInquiry(){
        log.info("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        GetUnclosedInquiryResponseDto answerResult = adminService.getUnclosedInquiry();
        log.info("[ADMIN] 답변 결과 = " + answerResult);
        return ResponseEntity.ok().body(answerResult);
    }

    @Operation(summary = "문의 답변하기", description = "문의에 대한 답변을 하는 api입니다.")
    @PutMapping("/inquiry/answer/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> answerInquiry(
           @PathVariable Long inquiryCid,
           String inquiryContent
    ){
        log.info("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        CommonResponseDto answerResult = adminService.answerInquiry(inquiryCid,inquiryContent);
        log.info("[ADMIN] 답변 결과 = " + answerResult);
        return ResponseEntity.ok().body(answerResult);
    }
}
