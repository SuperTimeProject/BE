package org.supercoding.supertime.user.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class LoginRequestDto {
    @Schema(description = "유저 이메일", example = "qwer1234@naver.com")
    private String userId;

    @Schema(description = "비밀번호", example = "qwerasdf1234")
    private String userPassword;

    // 첫 번째 생성자는 인증 전의 객체를 생성하고, 두 번째 생성자는 인증이 완료된 객체를 생성
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(userId, userPassword);
    }
}
