package org.supercoding.supertime.web.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
}
