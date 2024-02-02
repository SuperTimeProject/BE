package org.supercoding.supertime.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.config.security.TokenProvider;
import org.supercoding.supertime.repository.RefreshTokenRepository;
import org.supercoding.supertime.repository.SemesterRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.auth.SignupRequestDto;
import org.supercoding.supertime.web.dto.auth.TokenDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.auth.RefreshToken;
import org.supercoding.supertime.web.entity.enums.Roles;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    public CommonResponseDto login(LoginRequestDto loginInfo) {
        UserEntity user = userRepository.findByUserId(loginInfo.getUserId()).orElseThrow(()-> new NotFoundException("일치하는 유저가 존재하지 않습니다."));
        int isDeleted = user.getIsDeleted();

        if(isDeleted == 1) {
            throw new NotFoundException("탈퇴 처리된 유저입니다.");
        };

        if(!passwordEncoder.matches(loginInfo.getUserPassword(), user.getUserPassword()))
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");

        // TODO - 토큰 발급 과정 추가
        UsernamePasswordAuthenticationToken authenticationToken = loginInfo.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("[login] user role: "+user.getRoles());
        // 5. 토큰 발급
        //return tokenDto;


        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("로그인에 성공하였습니다.")
                .build();
    }

    public CommonResponseDto signup(SignupRequestDto signupInfo) {

        Boolean existUserId = userRepository.existsByUserId(signupInfo.getUserName());
        if(existUserId){
            throw new DataIntegrityViolationException("중복된 아이디가 존재합니다.");
        }

        Boolean existUserNickname = userRepository.existsByUserNickname(signupInfo.getUserNickname());
        if(existUserNickname){
            throw new DataIntegrityViolationException("중복된 닉네임이 존재합니다.");
        }

        // 패스워드 인코딩
        String password = passwordEncoder.encode(signupInfo.getUserPassword());

        UserEntity signupUser = UserEntity.builder()
                .userId(signupInfo.getUserId())
                .userNickname(signupInfo.getUserNickname())
                .userPassword(password)
                .semester(signupInfo.getSemesterCid())
                .roles(Roles.ROLE_ADMIN)
                .isDeleted(0)
                .varified(0)
                .build();

        userRepository.save(signupUser);

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("회원가입에 성공했습니다.")
                .build();
    }

    public CommonResponseDto emailDuplicateTest(String userEmail) {
        Boolean duplicateResult = userRepository.existsByUserId(userEmail);
        if (duplicateResult){
            throw new DataIntegrityViolationException("이미 사용중인 이메일입니다.");
        }

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("사용 가능한 email입니다.")
                .build();
    }

    public CommonResponseDto nicknameDuplicateTest(String nickname) {
        Boolean duplicateResult = userRepository.existsByUserNickname(nickname);
        if (duplicateResult){
            throw new DataIntegrityViolationException("이미 사용중인 닉네임입니다.");
        }

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("사용 가능한 닉네임입니다.")
                .build();
    }
}
