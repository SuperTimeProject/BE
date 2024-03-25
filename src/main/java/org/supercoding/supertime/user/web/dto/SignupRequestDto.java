package org.supercoding.supertime.user.web.dto;

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
public class SignupRequestDto {
    @Schema(description = "유저 이메일", example = "qwer1234@naver.com")
    private String userId;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "소속 기수", example = "1")
    private Long semesterCid;

    @Schema(description = "비밀번호", example = "qwerasdf1234")
    private String userPassword;
}
