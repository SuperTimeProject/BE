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
import org.supercoding.supertime.repository.BoardRepository;
import org.supercoding.supertime.repository.SemesterRepository;
import org.supercoding.supertime.repository.UserProfileRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.auth.LoginRequestDto;
import org.supercoding.supertime.web.dto.auth.SignupRequestDto;
import org.supercoding.supertime.web.dto.auth.TokenDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.auth.RefreshToken;
import org.supercoding.supertime.web.dto.auth.getUser.GetUserInfoDetailDto;
import org.supercoding.supertime.web.dto.auth.getUser.GetUserInfoResponseDto;
import org.supercoding.supertime.web.dto.auth.getUser.UserProfileDto;
import org.supercoding.supertime.web.dto.auth.getUser.UserSemesterDto;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;
import org.supercoding.supertime.web.entity.SemesterEntity;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.enums.Roles;
import org.supercoding.supertime.web.entity.user.UserEntity;
import org.supercoding.supertime.web.entity.user.UserProfileEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BoardRepository boardRepository;
    private final SemesterRepository semesterRepository;
    private final UserProfileRepository userProfileRepository;
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
      
        List<BoardEntity> userBoard = new ArrayList<>();
//        List<String> boardList = Arrays.asList("전체 게시판", "커뮤니티 게시판");
        SemesterEntity userSemester = semesterRepository.findById(signupInfo.getSemesterCid()).orElseThrow(()->new NotFoundException("기수가 존재하지 않습니다."));
        String[] boardList = {"전체 게시판", "커뮤니티 게시판", "기수 게시판 ("+userSemester.getSemesterName().toString()+")"};
        log.info("보드리스트" + boardList);

        for(String boardName : boardList){
            BoardEntity board = boardRepository.findByBoardName(boardName);
            userBoard.add(board);
        }

        UserEntity signupUser = UserEntity.builder()
                .userId(signupInfo.getUserId())
                .userNickname(signupInfo.getUserNickname())
                .userPassword(password)
                .semester(signupInfo.getSemesterCid())
                .roles(Roles.ROLE_USER)
                .boardList(userBoard)
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

    public GetUserInfoResponseDto getUserInfo(Long userCid) {
        UserEntity loggedInUser = userRepository.findById(userCid)
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));

        List<Long> boardList = new ArrayList<>();
        for(BoardEntity board:loggedInUser.getBoardList()){
            boardList.add(board.getBoardCid());
        }

        SemesterEntity semesterEntity = semesterRepository.findById(loggedInUser.getSemester())
                .orElseThrow(()->new NotFoundException("기수가 존재하지 않습니다."));
        UserSemesterDto semester = UserSemesterDto.builder()
                .semesterCid(semesterEntity.getSemesterCid())
                .semesterDetailName(semesterEntity.getSemesterDetailName())
                .isFull(semesterEntity.getIsFull())
                .build();

        UserProfileDto userProfile = null;
        if(loggedInUser.getUserProfileCid() != null){
        UserProfileEntity userProfileEntity = userProfileRepository.findById(loggedInUser.getUserProfileCid())
                .orElseThrow(()->new NotFoundException("찾는 프로필이 존재하지 않습니다."));
        userProfile = UserProfileDto.builder()
                .userProfileCid(userProfileEntity.getUserProfileCid())
                .userProfileFileName(userProfileEntity.getUserProfileFileName())
                .userProfileFilePath(userProfileEntity.getUserProfileFilePath())
                .build();
        }

        GetUserInfoDetailDto getUserInfoDetailDto = GetUserInfoDetailDto.builder()
                .userCid(loggedInUser.getUserCid())
                .userId(loggedInUser.getUserId())
                .userName(loggedInUser.getUserName())
                .userNickname(loggedInUser.getUserNickname())
                .part(loggedInUser.getPart())
                .role(loggedInUser.getRoles())
                .boardList(boardList)
                .semester(semester)
                .userProfile(userProfile)
                .build();

        return GetUserInfoResponseDto.builder()
                .code(200)
                .success(true)
                .message("유저 정보 불러오기 성공했습니다.")
                .getUserInfo(getUserInfoDetailDto)
                .build();
    }
}
