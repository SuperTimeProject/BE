package org.supercoding.supertime.user.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supercoding.supertime.golbal.auth.token.repository.RefreshTokenRepository;
import org.supercoding.supertime.golbal.auth.token.web.dto.TokenDto;
import org.supercoding.supertime.golbal.auth.token.web.entity.RefreshToken;
import org.supercoding.supertime.golbal.config.security.TokenProvider;
import org.supercoding.supertime.user.util.UserValidation;
import org.supercoding.supertime.user.web.dto.LoginRequestDto;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

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


}
