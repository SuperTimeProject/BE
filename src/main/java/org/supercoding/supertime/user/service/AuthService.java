package org.supercoding.supertime.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomMemberRepository;
import org.supercoding.supertime.chat.web.entity.ChatMessageEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.golbal.auth.token.repository.RefreshTokenRepository;
import org.supercoding.supertime.golbal.auth.token.web.dto.TokenDto;
import org.supercoding.supertime.golbal.auth.token.web.entity.RefreshToken;
import org.supercoding.supertime.golbal.config.security.TokenProvider;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;
import org.supercoding.supertime.user.repository.AuthStateRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.dto.SignupRequestDto;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AuthStateRepository authStateRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    private final UserValidation userValidation;


    private final int REFRESH_TOKEN_MAX_TIME = 10 * 60 * 60;

    /**
     * 기능 - 로그인
     *
     * @param loginInfo
     * @param res
     *
     * @return accessToken
     */
    @Transactional
    public String login(LoginRequestDto loginInfo, HttpServletResponse res) {
        userValidation.validateLogin(loginInfo);

        TokenDto tokenDto = createToken(loginInfo);
        createRefreshTokenCookie(res, tokenDto.getRefreshToken());

        return tokenDto.getAccessToken();
    }

    private TokenDto createToken(LoginRequestDto loginInfo) {
        UsernamePasswordAuthenticationToken authenticationToken = loginInfo.toAuthentication();
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        refreshTokenRepository.save(RefreshToken.from(authentication.getName(), tokenDto.getRefreshToken()));

        return tokenDto;
    }

    private void createRefreshTokenCookie(HttpServletResponse res, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_TIME);

        res.addCookie(refreshTokenCookie);
    }

    /**
     * 기능 - 이메일 중복 테스트
     * @param email
     */
    public void emailDuplicateTest(String email) {
        userValidation.validateDuplicateEmail(email);
    }

    /**
     * 기능 - 닉네임 중복 테스트
     * @param nickname
     */
    public void nicknameDuplicateTest(String nickname) {
        userValidation.validateDuplicateNickname(nickname);
    }

    @Transactional
    public void signup(SignupRequestDto signupInfo) {
        emailDuplicateTest(signupInfo.getUserId());
        nicknameDuplicateTest(signupInfo.getUserNickname());

        SemesterEntity userSemester = userValidation.findSemester(signupInfo.getSemesterCid());
        List<BoardEntity> userBoard = createUserBoardList(userSemester.getSemesterName());

        String password = passwordEncoder.encode(signupInfo.getUserPassword());

        UserEntity signupUser = UserEntity.from(signupInfo, password, userBoard);
        userRepository.save(signupUser);

        createAuthState(signupUser.getUserId());

        enterChatRoom(signupUser.getUserId(), userSemester.getSemesterName().toString());
    }

    private List<BoardEntity> createUserBoardList(int semesterName) {
        List<BoardEntity> userBoard = new ArrayList<>();
        String[] boardList = {"전체 게시판", "커뮤니티 게시판", "기수 게시판 ("+semesterName+")"};

        for(String boardName : boardList) {
            userBoard.add(userValidation.findBoard(boardName));
        }

        return userBoard;
    }

    private void createAuthState(String signupUserId) {
        AuthStateEntity newAuth = AuthStateEntity.from(signupUserId);
        authStateRepository.save(newAuth);
    }

    private void enterChatRoom(String userid, String userSemesterName) {
        UserEntity createdUser = userValidation.validateExistUser(userid);
        ChatRoomEntity chatRoom = userValidation.validateExistChatRoom(userSemesterName);
        ChatRoomMemberEntity chatRoomMember = ChatRoomMemberEntity.from(createdUser, chatRoom);

        chatRoomMemberRepository.save(chatRoomMember);

        ChatMessageEntity chatMessage = ChatMessageEntity.enter(createdUser, chatRoom);
        chatMessageRepository.save(chatMessage);
    }

}
