package org.supercoding.supertime.golbal.auth.token.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.user.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor//(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class TokenUserDto {
    @Schema(description = "유저 이메일", example = "qwer1234@naver.com")
    private String userId;

    @Schema(description = "비밀번호", example = "qwerasdf1234")
    private String userPassword;

    @Schema(description = "유저권한", example = "ROLE_USER")
    private String roles;

    public static TokenUserDto toDto(UserEntity userEntity){
        return TokenUserDto.builder()
                .userId(userEntity.getUserId())
                .userPassword(userEntity.getUserPassword())
                .roles(userEntity.getRoles().getType())
                .build();
    }
}