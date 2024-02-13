package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.service.AuthService;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.auth.SignupRequestDto;
import org.supercoding.supertime.web.dto.user.getUserDto.GetUserInfoResponseDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "회원관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인을 다루는 api입니다.")
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto loginInfo, HttpServletResponse httpServletResponse){
        log.info("[AUTH] 로그인 요청이 들어왔습니다.");
        CommonResponseDto loginResult = authService.login(loginInfo,httpServletResponse);
        log.info("[AUTH] 로그인 결과 = " + loginResult);
        return ResponseEntity.ok().body(loginResult);
    }

    @Operation(summary = "회원가입", description = "회원가입을 다루는 api입니다.")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto> signup(@RequestBody SignupRequestDto signupInfo){
        log.info("[AUTH] 회원가입 요청이 들어왔습니다.");
        CommonResponseDto signupResult = authService.signup(signupInfo);
        log.info("[AUTH] 회원가입 결과 = " + signupResult);
        return ResponseEntity.ok().body(signupResult);
    }

    @Operation(summary = "이메일 중복확인", description = "이메일 중복을 확인하는 api입니다.")
    @GetMapping("/duplicateTest/email")
    public ResponseEntity<CommonResponseDto> emailDuplicateTest(@RequestParam String userEmail){
        log.info("[DUPLICATE] 이메일 중복확인 요청이 들어왔습니다.");
        CommonResponseDto duplicateTestResult = authService.emailDuplicateTest(userEmail);
        log.info("[DUPLICATE] 이메일 중복확인 요청 결과 = " + duplicateTestResult);
        return ResponseEntity.ok().body(duplicateTestResult);
    }

    @Operation(summary = "닉네임 중복확인", description = "닉네임 중복을 확인하는 api입니다.")
    @GetMapping("/duplicateTest/nickname")
    public ResponseEntity<CommonResponseDto> nicknameDuplicateTest(@RequestParam String nickname){
        log.info("[DUPLICATE] 이메일 중복확인 요청이 들어왔습니다.");
        CommonResponseDto duplicateTestResult = authService.nicknameDuplicateTest(nickname);
        log.info("[DUPLICATE] 이메일 중복확인 요청 결과 = " + duplicateTestResult);
        return ResponseEntity.ok().body(duplicateTestResult);
    }

    @Operation(summary = "로그인 유저 불러오기", description = "로그인한 유저의 정보를 불러오는 api입니다.")
    @GetMapping("/getUserInfo")
    public ResponseEntity<GetUserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user){
        log.info("[GET_USER] 유저 정보를 불러오는 요청이 들어왔습니다.");
        GetUserInfoResponseDto getUserInfoResult = authService.getUserInfo(user);
        log.info("[GET_USER] 유저 정보 결과 = " + getUserInfoResult);

        return ResponseEntity.ok(getUserInfoResult);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 api입니다.")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @AuthenticationPrincipal User user){
        log.info("[LOGOUT] 로그아웃 요청이 들어왔습니다.");
        //CommonResponseDto logoutInfoResult = authService.logout(user);
        new SecurityContextLogoutHandler().logout(request,response,authentication);
        log.info("[LOGOUT] 로그아웃 성공");

        CommonResponseDto logoutInfoResult = CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("로그아웃 성공")
                .build();

        return ResponseEntity.ok(logoutInfoResult);
    }

}
