package org.supercoding.supertime.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.supercoding.supertime.service.AuthService;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "회원관련 API", description = "유저 접속 관련")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", description = "로그인을 다루는 api입니다.")
    @PostMapping("/login")
    public ResponseEntity<CommonResponseDto> login(@RequestBody LoginRequestDto loginInfo){
        log.info("[AUTH] 로그인 요청이 들어왔습니다.");
        CommonResponseDto loginResult = authService.login(loginInfo);
        log.info("[AUTH] 로그인 결과 = " + loginResult);
        return ResponseEntity.ok().body(loginResult);
    }
}
