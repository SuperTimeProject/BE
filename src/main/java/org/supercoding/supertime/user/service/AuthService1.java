package org.supercoding.supertime.user.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.golbal.auth.token.repository.RefreshTokenRepository;
import org.supercoding.supertime.golbal.web.advice.CustomDataIntegerityCiolationException;
import org.supercoding.supertime.semester.repository.SemesterRepository;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserProfileRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.board.repository.BoardRepository;
import org.supercoding.supertime.chat.web.entity.ChatMessageEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.chat.web.entity.MessageType;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomMemberRepository;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.golbal.config.security.TokenProvider;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.dto.SignupRequestDto;
import org.supercoding.supertime.golbal.auth.token.web.dto.TokenDto;
import org.supercoding.supertime.golbal.auth.token.web.dto.TokenRequestDto;
import org.supercoding.supertime.user.web.dto.getUserInfo.GetUserInfoBoardInfoDto;
import org.supercoding.supertime.user.web.dto.getUserInfo.GetUserInfoDetailDto;
import org.supercoding.supertime.user.web.dto.getUserInfo.GetUserInfoResponseDto;
import org.supercoding.supertime.user.web.dto.getUserDto.UserProfileDto;
import org.supercoding.supertime.user.web.dto.getUserDto.UserSemesterDto;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.golbal.auth.token.web.entity.RefreshToken;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Valified;
import org.supercoding.supertime.user.web.entity.user.UserEntity;
import org.supercoding.supertime.user.web.entity.user.UserProfileEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService1 {
  
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BoardRepository boardRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AuthStateRepository authStateRepository;

    public CommonResponseDto setRole(Long userCid,Roles role){
        UserEntity user = userRepository.findByUserCid(userCid)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));

        user.setRoles(role);

        userRepository.save(user);

        return CommonResponseDto.successResponse("관리자로 변경에 성공했습니다.");
    }


    public CommonResponseDto logout(User user){

        String userCid = user.getUsername();
        //TODO 로그아웃 구현
        RefreshToken token = refreshTokenRepository.findByKey(user.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("삭제하려는 토큰이 존재하지 않습니다."));

        refreshTokenRepository.delete(token);

        return CommonResponseDto.builder()
                .success(true)
                .code(200)
                .message("로그아웃 성공 했습니다.")
                .build();
    }
}
