package org.supercoding.supertime.user.web.controller;

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
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailDto;
import org.supercoding.supertime.user.service.MyPageService;
import org.supercoding.supertime.user.service.UserService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryDetailResponseDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryRequestDto;
import org.supercoding.supertime.inquiry.web.dto.InquiryResponseDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
@Builder
@Tag(name = "마이페이지 API")
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;

    @Operation(summary = "유저 프로필 수정", description = "유저의 프로필 이미지를 수정하는 API입니다.")
    @PutMapping(value = "/profile-image",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> editProfileImage(
            @AuthenticationPrincipal User user,
            @RequestPart(name = "userProfileImage", required = false) MultipartFile newProfileImage
    ) {
        log.debug("[MY_PAGE] 프로필 이미지 수정 요청이 들어왔습니다.");
        myPageService.editProfileImage(user, newProfileImage);
        log.debug("[MY_PAGE] 프로필 이미지를 성공적으로 수정하였습니다.");

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(CommonResponseDto.successResponse("유저 프로필 이미지 수정에 성공했습니다."));
    }

    @Operation(summary = "유저 닉네임 수정", description = "유저의 닉네임을 수정하는 API입니다.")
    @PutMapping(value = "/profile-nickname",consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponseDto> editNickname(
            @AuthenticationPrincipal User user,
            @Parameter String newNickname
    ) {
        log.debug("[MY_PAGE] 닉네임 수정 요청이 들어왔습니다.");
        myPageService.editNickname(user, newNickname);
        log.debug("[MY_PAGE] 닉네임을 성공적으로 수정하였습니다.");

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponseDto.successResponse("유저 닉네임 수정에 성공했습니다."));
    }

    @Operation(summary = "주특기 선택", description = "주특기를 선택하는 api입니다.")
    @PutMapping(value = "/part/{partName}")
    public ResponseEntity<CommonResponseDto> selectPart(
            @AuthenticationPrincipal User user,
            @PathVariable String partName
    ){
        log.debug("[MY_PAGE] 주특기 선택 요청이 들어왔습니다.");
        myPageService.selectPart(user,partName);
        log.debug("[MY_PAGE] 주특기를 성곡적으로 변경하였습니다");
        return ResponseEntity.ok(CommonResponseDto.successResponse("성공적으로 주특기를 선택하였습니다."));
    }

    @Operation(summary = "주특기 확정", description = "주특기를 확정하는 api입니다.")
    @PutMapping(value = "/part/confirmed")
    public ResponseEntity<CommonResponseDto> confirmedPart(
            @AuthenticationPrincipal User user
    ){
        log.info("[MY_PAGE] 주특기 선택 요청이 들어왔습니다.");
        CommonResponseDto selectPartResult = userService.confirmedPart(user);
        log.info("[MY_PAGE] 주특기 선택 결과 = " + selectPartResult);
        return ResponseEntity.ok(selectPartResult);
    }

    @Operation(summary = "프로필 이미지 삭제", description = "프로필 이미지를 삭제하는 api입니다.")
    @PutMapping(value = "/info/profile-image")
    public ResponseEntity<CommonResponseDto> deleteProfileImage(
            @AuthenticationPrincipal User user
    ){
        log.info("[USER] 프로필 이미지 삭제 요청이 들어왔습니다.");
        CommonResponseDto deleteImgResult = userService.deleteProfileImage(user);
        log.info("[USER] 프로필 이미지 삭제 결과 = " + deleteImgResult);
        return ResponseEntity.ok(deleteImgResult);
    }


}
