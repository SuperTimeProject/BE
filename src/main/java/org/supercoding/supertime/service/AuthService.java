package org.supercoding.supertime.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    public CommonResponseDto login(LoginRequestDto loginInfo) {
        UserEntity user = userRepository.findByUserId(loginInfo.getUserId()).orElseThrow(()-> new NotFoundException("일치하는 유저가 존재하지 않습니다."));
        String isDeleted = user.getIsDeleted();

        if(isDeleted == "1") {
            throw new NotFoundException("탈퇴 처리된 유저입니다.");
        };
        // TODO - security 구현 후 'passwordEncoder.matches'를 사용한 비밀번호 확인 절차로 수정
        if(!user.getUserPassword().equals(loginInfo.getUserPassword())){
            // TODO - security에서 제공하는 예외처리로 수정
            throw new NotFoundException("비밀번호가 일치하지 않습니다.");
            // throw new BadCredentialException("비밀번호가 일치하지 않습니다.");
        }

        // TODO - 토큰 발급 과정 추가

        return CommonResponseDto.builder()
                .code(200)
                .success(true)
                .message("로그인에 성고하였습니다.")
                .build();
    }
}
