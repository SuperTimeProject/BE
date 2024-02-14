package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.supercoding.supertime.service.user.UserService;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryRequestDto;
import org.supercoding.supertime.web.dto.inquiry.InquiryResponseDto;
import org.supercoding.supertime.web.dto.user.EditUserInfoRequestDto;
import org.supercoding.supertime.web.dto.user.getUserDto.GetUserPageResponseDto;
import org.supercoding.supertime.web.entity.enums.Part;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Builder
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
    public ResponseEntity<CommonResponseDto> editUserInfo(@AuthenticationPrincipal User user, EditUserInfoRequestDto editUserInfoRequestDto){
        log.info("[USER] 유저 정보 조회 요청이 들어왔습니다.");
        CommonResponseDto editUserInfoResult = userService.editUserInfo(user,editUserInfoRequestDto);
        log.info("[USER] 유저 정보 조회 결과 = " + editUserInfoResult);
        return ResponseEntity.ok(editUserInfoResult);
    }

    @Operation(summary = "문의 조회", description = "문의한 기록을 불러오는 api입니다.")
    @GetMapping("/inquiry")
    public ResponseEntity<InquiryResponseDto> getInquiryHistory(@AuthenticationPrincipal User user){
        log.info("[USER] 유저 문의 기록 조회 요청이 들어왔습니다.");
        InquiryResponseDto getInquiryHistoryResult = userService.getInquiryHistory(user);
        log.info("[USER] 유저 문의 기록 조회 결과 = " + getInquiryHistoryResult);
        return ResponseEntity.ok(getInquiryHistoryResult);

    }

    //내가 쓴 글 조회(게시판 별로)
    @Operation(summary = "문의하기", description = "관리자에게 문의하는 api입니다.")
    @PostMapping(value = "/inquiry/request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> inquiry(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "inquiryInfo") @Parameter(schema = @Schema(type = "string", format = "binary")) InquiryRequestDto inquiryRequestDto,
            @RequestPart(name = "inquiryImage", required = false) List<MultipartFile> inquiryImage
    ){
        log.info("[USER] 유저 문의하기 요청이 들어왔습니다.");
        CommonResponseDto inquiryResult = userService.createInquiry(user,inquiryRequestDto,inquiryImage);
        log.info("[USER] 유저 문의하기 결과 = " + inquiryResult);
        return ResponseEntity.ok(inquiryResult);
    }

    @Operation(summary = "주특기 선택", description = "주특기를 선택하는 api입니다.")
    @PutMapping(value = "/part/{partName}")
    public ResponseEntity<CommonResponseDto> selectPart(
            @AuthenticationPrincipal User user,
            @PathVariable String partName
    ){
        log.info("[USER] 주특기 선택 요청이 들어왔습니다.");
        CommonResponseDto selectPartResult = userService.selectPart(user,partName);
        log.info("[USER] 주특기 선택 결과 = " + selectPartResult);
        return ResponseEntity.ok(selectPartResult);
    }



}