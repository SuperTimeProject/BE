package org.supercoding.supertime.admin.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.admin.service.AdminService;
import org.supercoding.supertime.admin.web.dto.inquiry.GetInquiryDetail;
import org.supercoding.supertime.admin.web.dto.inquiry.GetInquiryRes;
import org.supercoding.supertime.admin.web.dto.verified.GetVerifiedUserDetailResponseDto;
import org.supercoding.supertime.golbal.web.enums.*;
import org.supercoding.supertime.admin.web.dto.verified.GetVerifiedUserDetailDto;
import org.supercoding.supertime.admin.web.dto.verified.GetVerifiedUserDto;
import org.supercoding.supertime.admin.web.dto.UpdateUserInfoRequestDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 관련 API")
public class AdminController {

    private final AdminService adminService;


    @Operation(summary = "권한 변경하기", description = "유저 역할을 변경하는 api입니다.")
    @PutMapping("/set-role")
    public ResponseEntity<CommonResponseDto> setRole(
            @RequestParam Long userCid,
            @RequestParam Roles role
    ){
        log.debug("[ADMIN] 역할 변경 요청이 들어왔습니다.");
        adminService.changeRole(userCid, role);
        log.debug("[ADMIN] 역할을 성공적으로 변경했습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("역할 변경에 성공했습니다."));
    }


    @Operation(summary = "회원 인증상태 별 조회", description = "회원인증 인증상태에 따른 대기내역을 조회하는 api입니다.")
    @GetMapping("/pending-user/{page}")
    public ResponseEntity<GetVerifiedUserDto> getUserByValified(
            @RequestParam Verified verified,
            @PathVariable int page
    ){
        log.debug("[ADMIN] 회원인증 인증상태 별 조회 요청이 들어왔습니다.");
        List<GetVerifiedUserDetailDto> userDetailDtoList = adminService.findUserByVerified(verified,page);
        log.debug("[ADMIN] 인증상태 별 유저를 성공적으로 조회했습니다.");
        return ResponseEntity.ok().body(GetVerifiedUserDto.successResponse("인증상태별 유저를 조회하였습니다.", userDetailDtoList));
    }

    @Operation(summary = "회원 인증요청 상세 조회", description = "회원 인증요청 상세내용을 조회하는 api입니다.")
    @GetMapping("/pending-user/detail/{userId}")
    public ResponseEntity<GetVerifiedUserDetailResponseDto> getValifiedDetail(
            @PathVariable String userId
    ){
        log.debug("[ADMIN] 회원의 인증 상세내용 조회 요청이 들어왔습니다.");
        GetVerifiedUserDetailDto userDetail = adminService.getVerifiedUserDetail(userId);
        log.debug("[ADMIN] 회원의 인증 상세내용을 조회하였습니다.");
        return ResponseEntity.ok().body(GetVerifiedUserDetailResponseDto.successResponse("회원 인증 상세 내용을 조회했습니다.", userDetail));
    }

    @Operation(summary = "회원 인증 관리", description = "회원 인증상태를 변경하는 api입니다.")
    @PutMapping("/verification")
    public ResponseEntity<CommonResponseDto> verification(
            @RequestParam String userId,
            @RequestParam Verified verified
    ){
        log.debug("[ADMIN] 회원인증 요청이 들어왔습니다.");
        adminService.changeVerification(userId, verified);
        log.debug("[ADMIN] 인증상태를 성공적으로 수정했습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("성공적으로 인증상태를 수정하였습니다."));
    }

    @Operation(summary = "회원 정보 수정", description = "회원의 모든 정보를 수정 할 수 있는 api입니다.")
    @PutMapping("/users-info")
    public ResponseEntity<CommonResponseDto> updateUserInfo(
            @RequestPart(name="userInfo")
            @Parameter(schema = @Schema(type = "string", format = "binary"))
            UpdateUserInfoRequestDto updateUserInfoRequestDto
            ){
        log.debug("[ADMIN] 회원인증 요청이 들어왔습니다.");
        CommonResponseDto updateUserInfoResult = adminService.updateUserInfo(updateUserInfoRequestDto);
        log.debug("[ADMIN] 인증 결과 = " + updateUserInfoResult);
        return ResponseEntity.ok().body(updateUserInfoResult);
    }


    @Operation(summary = "열린 문의 조회하기", description = "문의기록을 조회하는 api입니다.")
    @GetMapping("/open-inquiry/{page}")
    public ResponseEntity<GetInquiryRes> getOpenInquiry(
            @PathVariable int page
    ){
        log.debug("[ADMIN] 열린 문의 조회 요청이 들어왔습니다.");
        List<GetInquiryDetail> inquiryList = adminService.getInquiryByIsClosed(page, InquiryClosed.OPEN);
        log.debug("[ADMIN] 열린 문의를 성공적으로 불러왔습니다." );
        return ResponseEntity.ok().body(GetInquiryRes.from("열려있는 문의를 조회했습니다.", inquiryList));
    }

    @Operation(summary = "닫힌 문의 조회하기", description = "문의기록을 조회하는 api입니다.")
    @GetMapping("/closed-inquiry/{page}")
    public ResponseEntity<GetInquiryRes> getClosedInquiry(
            @PathVariable int page
    ){
        log.debug("[ADMIN] 닫힌 문의 조회 요청이 들어왔습니다.");
        List<GetInquiryDetail> inquiryList = adminService.getInquiryByIsClosed(page, InquiryClosed.CLOSED);
        log.debug("[ADMIN] 닫힌 문의를 성공적으로 불러왔습니다." );
        return ResponseEntity.ok().body(GetInquiryRes.from("닫힌 문의를 조회했습니다.", inquiryList));
    }

    @Operation(summary = "문의 답변하기", description = "문의에 대한 답변을 하는 api입니다.")
    @PutMapping("/inquiry/answer/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> answerInquiry(
           @PathVariable Long inquiryCid,
           @RequestParam String inquiryContent
    ){
        log.debug("[ADMIN] 문의 답변 요청이 들어왔습니다.");
        adminService.answerInquiry(inquiryCid,inquiryContent);
        log.debug("[ADMIN] 문의 답변을 성공적으로 추가했습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("문의 답변에 성공했습니다."));
    }

    @Operation(summary = "문의 삭제하기", description = "문의를 삭제하는 api입니다.")
    @DeleteMapping("/inquiry/{inquiryCid}")
    public ResponseEntity<CommonResponseDto> deleteInquiry(
            @PathVariable Long inquiryCid
    ){
        log.debug("[ADMIN] 문의 삭제 요청이 들어왔습니다.");
        adminService.deleteInquiry(inquiryCid);
        log.debug("[ADMIN] 답변를 성공적으로 삭제했습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("문의 삭제에 성공했습니다."));
    }
}
