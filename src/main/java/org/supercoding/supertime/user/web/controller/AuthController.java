package org.supercoding.supertime.user.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.user.service.AuthService;
import org.supercoding.supertime.user.service.AuthService1;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.dto.LoginResponseDto;
import org.supercoding.supertime.user.web.dto.SignupRequestDto;
import org.supercoding.supertime.user.web.dto.getUserInfo.GetUserInfoResponseDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.golbal.web.enums.Roles;

@RestController
@Slf4j
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "회원관련 API")
public class AuthController {
    private final AuthService1 authService1;
    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인을 다루는 api입니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginInfo, HttpServletResponse httpServletResponse){
        log.debug("[AUTH] 로그인 요청이 들어왔습니다.");
        String accessToken = authService.login(loginInfo, httpServletResponse);
        log.debug("[AUTH] 로그인이 성공적으로 이루어졌습니다.");
        return ResponseEntity.ok().body(LoginResponseDto.successResponse("로그인에 성공했습니다.", accessToken));
    }

    @Operation(summary = "회원가입", description = "회원가입을 다루는 api입니다.")
    @PostMapping("/signup")
    public ResponseEntity<CommonResponseDto> signup(@RequestBody SignupRequestDto signupInfo){
        log.info("[AUTH] 회원가입 요청이 들어왔습니다.");
        CommonResponseDto signupResult = authService1.signup(signupInfo);
        log.info("[AUTH] 회원가입 결과 = " + signupResult);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResult);
    }

    @Operation(summary = "이메일 중복확인", description = "이메일 중복을 확인하는 api입니다.")
    @GetMapping("/duplicate-test/email")
    public ResponseEntity<CommonResponseDto> emailDuplicateTest(@RequestParam String userEmail){
        log.info("[DUPLICATE] 이메일 중복확인 요청이 들어왔습니다.");
        CommonResponseDto duplicateTestResult = authService1.emailDuplicateTest(userEmail);
        log.info("[DUPLICATE] 이메일 중복확인 요청 결과 = " + duplicateTestResult);
        return ResponseEntity.ok().body(duplicateTestResult);
    }

    @Operation(summary = "닉네임 중복확인", description = "닉네임 중복을 확인하는 api입니다.")
    @GetMapping("/duplicate-test/nickname")
    public ResponseEntity<CommonResponseDto> nicknameDuplicateTest(@RequestParam String nickname){
        log.info("[DUPLICATE] 이메일 중복확인 요청이 들어왔습니다.");
        CommonResponseDto duplicateTestResult = authService1.nicknameDuplicateTest(nickname);
        log.info("[DUPLICATE] 이메일 중복확인 요청 결과 = " + duplicateTestResult);
        return ResponseEntity.ok().body(duplicateTestResult);
    }

    @Operation(summary = "로그인 유저 불러오기", description = "로그인한 유저의 정보를 불러오는 api입니다.")
    @GetMapping("/user-info")
    public ResponseEntity<GetUserInfoResponseDto> getUserInfo(@AuthenticationPrincipal User user){
        log.info("[GET_USER] 유저 정보를 불러오는 요청이 들어왔습니다.");
        GetUserInfoResponseDto getUserInfoResult = authService1.getUserInfo(user);
        log.info("[GET_USER] 유저 정보 결과 = " + getUserInfoResult);

        return ResponseEntity.ok(getUserInfoResult);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 api입니다.")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponseDto> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication, @AuthenticationPrincipal User user){
        log.info("[LOGOUT] 로그아웃 요청이 들어왔습니다.");
        //CommonResponseDto logoutInfoResult = authService1.logout(user);
        new SecurityContextLogoutHandler().logout(request,response,authentication);
        log.info("[LOGOUT] 로그아웃 성공");

        CommonResponseDto logoutInfoResult = CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("로그아웃 성공")
                .build();

        return ResponseEntity.ok(logoutInfoResult);
    }

    @Operation(summary = "권한 변경하기", description = "유저 역할을 변경하는 api입니다.")
    @PutMapping("/set-role")
    public ResponseEntity<CommonResponseDto> setRole(
            @RequestParam Long UserCid,
            @RequestParam Roles role
    ){
        log.info("[ADMIN] 역할 변경 요청이 들어왔습니다.");
        CommonResponseDto setRoleResult = authService1.setRole(UserCid,role);
        log.info("[ADMIN] 답변 결과 = " + setRoleResult);
        return ResponseEntity.ok().body(setRoleResult);
    }
}
