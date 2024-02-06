package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.service.user.UserService;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.user.getUserDto.GetUserPageResponseDto;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Builder
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "마이페이지 API")
public class MyPageController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회", description = "로그인한 유저 정보를 보여주는 api입니다.")
    @GetMapping("/info")
    public ResponseEntity<GetUserPageResponseDto> userInfo(@AuthenticationPrincipal User user){
        log.info("[USER] 유저 정보 조회 요청이 들어왔습니다.");
        GetUserPageResponseDto getUserInfoResult = userService.GetUserInfo(user);
        log.info("[USER] 유저 정보 조회 결과 = " + getUserInfoResult);
        return ResponseEntity.ok(getUserInfoResult);
    }

    @Operation(summary = "유저 정보 수정", description = "로그인한 유저 정보를 수정하는 api입니다.")
    @PutMapping("/info/edit")
    public ResponseEntity<CommonResponseDto> editUserInfo(){
        log.info("[USER] 유저 정보 조회 요청이 들어왔습니다.");
        CommonResponseDto editUserInfoResult = userService.editUserInfo();
        log.info("[USER] 유저 정보 조회 결과 = " + editUserInfoResult);
        return ResponseEntity.ok(editUserInfoResult);
    }

    @Operation(summary = "문의 조회", description = "문의한 기록을 불러오는 api입니다.")
    @GetMapping("/inquiry")
    public ResponseEntity<CommonResponseDto> getInquiryHistory(){
        log.info("[USER] 유저 정보 조회 요청이 들어왔습니다.");
        CommonResponseDto getInquiryHistoryResult = userService.getInquiryHistory();
        log.info("[USER] 유저 정보 조회 결과 = " + getInquiryHistoryResult);
        return ResponseEntity.ok(getInquiryHistoryResult);

    }

    @Operation(summary = "문의하기", description = "관리자에게 문의하는 api입니다.")
    @PostMapping("/inquiry/request")
    public ResponseEntity<CommonResponseDto> inquiry(){
        log.info("[USER] 유저 정보 조회 요청이 들어왔습니다.");
        CommonResponseDto inquiryResult = userService.inquiry();
        log.info("[USER] 유저 정보 조회 결과 = " + inquiryResult);
        return ResponseEntity.ok(inquiryResult);
    }



}
