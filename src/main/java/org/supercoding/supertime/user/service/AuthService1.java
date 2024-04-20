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
    public CommonResponseDto login(LoginRequestDto loginInfo, HttpServletResponse httpServletResponse) {
        UserEntity user = userRepository.findByUserId(loginInfo.getUserId()).orElseThrow(()-> new CustomNotFoundException("일치하는 유저가 존재하지 않습니다."));
        int isDeleted = user.getIsDeleted();

        if(isDeleted == 1) {
            throw new CustomNotFoundException("탈퇴 처리된 유저입니다.");
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
        log.info("[login] user role: " + user.getRoles());
        // 5. 토큰 발급
        //return tokenDto;

//      1. accesstoken 2. refreshtoken
//        ResponseCookie responseCookie = ResponseCookie
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("None")
//                .maxAge()
//                .path("/")
//                .build();

        httpServletResponse.setHeader("Authorization", tokenDto.getAccessToken());

        return CommonResponseDto.successResponse("로그인에 성공했습니다.");
    }

    @Transactional
    public CommonResponseDto signup(SignupRequestDto signupInfo) {
        Boolean existUserId = userRepository.existsByUserId(signupInfo.getUserName());
        if(existUserId){
            throw new DataIntegrityViolationException("중복된 아이디가 존재합니다.");
        }

        Boolean existUserNickname = userRepository.existsByUserNickname(signupInfo.getUserNickname());
        if(existUserNickname){
            throw new DataIntegrityViolationException("중복된 닉네임이 존재합니다.");
        }

        List<BoardEntity> userBoard = new ArrayList<>();
        SemesterEntity userSemester = semesterRepository.findById(signupInfo.getSemesterCid()).orElseThrow(()->new CustomNotFoundException("기수가 존재하지 않습니다."));
        String[] boardList = {"전체 게시판", "커뮤니티 게시판", "기수 게시판 ("+userSemester.getSemesterName().toString()+")"};

//        for(String boardName : boardList){
//            BoardEntity board = boardRepository.findByBoardName(boardName);
//            userBoard.add(board);
//        }

        // 패스워드 인코딩
        String password = passwordEncoder.encode(signupInfo.getUserPassword());

        UserEntity signupUser = UserEntity.builder()
                .userId(signupInfo.getUserId())
                .userName(signupInfo.getUserName())
                .userNickname(signupInfo.getUserNickname())
                .userPassword(password)
                .semester(signupInfo.getSemesterCid())
                .boardList(userBoard)
                .roles(Roles.ROLE_USER)
                .part(Part.PART_UNDEFINED)
                .isDeleted(0)
                .valified(Valified.NEEDED) // 인증에 관한 api 구현 전까지 인증 완료상태 반환
                .build();

        userRepository.save(signupUser);

        // 기수 채팅방 입장
        UserEntity createdUser = userRepository.findByUserId(signupUser.getUserId())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 유저가 없습니다."));

        ChatRoomEntity chatRoom = chatRoomRepository.findByChatRoomName(userSemester.getSemesterName().toString())
                .orElseThrow(()-> new CustomNotFoundException("일치하는 채팅방이 존재하지 않습니다."));

        ChatRoomMemberEntity chatRoomMember = ChatRoomMemberEntity.builder()
                .user(createdUser)
                .chatRoom(chatRoom)
                .build();
        chatRoomMemberRepository.save(chatRoomMember);

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(MessageType.ENTER)
                .user(createdUser)
                .chatRoom(chatRoom)
                .chatMessageContent(createdUser.getUserNickname()+"유저가 들어왔습니다.")
                .build();
        chatMessageRepository.save(chatMessage);
      
        // 인증상태 
        AuthStateEntity newAuth = AuthStateEntity.builder()
                .userId(signupUser.getUserId())
                .valified(Valified.NEEDED)
                .build();

        authStateRepository.save(newAuth);


        return CommonResponseDto.createSuccessResponse("회원가입에 성공했습니다.");
    }


    @Transactional
    public CommonResponseDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
        //return tokenDto;

        return CommonResponseDto.createSuccessResponse("토큰 갱신에 성공했습니다.");
    }


    public CommonResponseDto emailDuplicateTest(String userEmail) {
        Boolean duplicateResult = userRepository.existsByUserId(userEmail);
        if (duplicateResult){
            throw new CustomDataIntegerityCiolationException("이미 사용중인 이메일입니다.");
        }

        return CommonResponseDto.successResponse("사용 가능한 email 입니다.");
    }

    public CommonResponseDto nicknameDuplicateTest(String nickname) {
        Boolean duplicateResult = userRepository.existsByUserNickname(nickname);
        if (duplicateResult){
            throw new CustomDataIntegerityCiolationException("이미 사용중인 닉네임입니다.");
        }

        return CommonResponseDto.successResponse("사용 가능한 닉네임 입니다.");
    }


    public GetUserInfoResponseDto getUserInfo(User user) {
        UserEntity loggedInUser = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new CustomNotFoundException("유저가 존재하지 않습니다."));

        List<GetUserInfoBoardInfoDto> boardList = new ArrayList<>();
        for(BoardEntity board:loggedInUser.getBoardList()){
            GetUserInfoBoardInfoDto boardInfo = GetUserInfoBoardInfoDto.builder()
                    .boardCid(board.getBoardCid())
                    .boardName(board.getBoardName())
                    .build();

            boardList.add(boardInfo);
        }

        SemesterEntity semesterEntity = semesterRepository.findById(loggedInUser.getSemester())
                .orElseThrow(()->new CustomNotFoundException("기수가 존재하지 않습니다."));
        UserSemesterDto semester = UserSemesterDto.builder()
                .semesterCid(semesterEntity.getSemesterCid())
                .semesterDetailName(semesterEntity.getSemesterDetailName())
                .isFull(semesterEntity.getIsFull())
                .build();

        UserProfileDto userProfile = null;
        if(loggedInUser.getUserProfileCid() != null){
            UserProfileEntity userProfileEntity = userProfileRepository.findById(loggedInUser.getUserProfileCid())
                    .orElseThrow(()->new CustomNotFoundException("찾는 프로필이 존재하지 않습니다."));
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
                .valified(loggedInUser.getValified())
                .build();

        return GetUserInfoResponseDto.successResponse("유저 정보를 성공적으로 불러왔습니다.", getUserInfoDetailDto);
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
