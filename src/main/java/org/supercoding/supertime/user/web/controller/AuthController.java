package org.supercoding.supertime.user.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.user.service.AuthService;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.dto.LoginResponseDto;
import org.supercoding.supertime.user.web.dto.SignupRequestDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/public/auth")
@RequiredArgsConstructor
@Tag(name = "회원관련 API")
public class AuthController {
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
        log.debug("[AUTH] 회원가입 요청이 들어왔습니다.");
        authService.signup(signupInfo);
        log.debug("[AUTH] 회원가입이 성공적으로 이루어졌습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponseDto.createSuccessResponse("회원가입에 성공했습니다."));
    }

    @Operation(summary = "이메일 중복확인", description = "이메일 중복을 확인하는 api입니다.")
    @GetMapping("/duplicate-test/email")
    public ResponseEntity<CommonResponseDto> emailDuplicateTest(@RequestParam String userEmail){
        log.debug("[DUPLICATE] 이메일 중복확인 요청이 들어왔습니다.");
        authService.emailDuplicateTest(userEmail);
        log.debug("[DUPLICATE] 이메일 중복검사가 정상적으로 이루어 졌습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("사용가능한 이메일입니다."));
    }

    @Operation(summary = "닉네임 중복확인", description = "닉네임 중복을 확인하는 api입니다.")
    @GetMapping("/duplicate-test/nickname")
    public ResponseEntity<CommonResponseDto> nicknameDuplicateTest(@RequestParam String nickname){
        log.debug("[DUPLICATE] 닉네임 중복확인 요청이 들어왔습니다.");
        authService.nicknameDuplicateTest(nickname);
        log.debug("[DUPLICATE] 닉네임 중복검사가 정상적으로 이루어 졌습니다.");
        return ResponseEntity.ok().body(CommonResponseDto.successResponse("사용가능한 닉네임입니다."));
    }
}
