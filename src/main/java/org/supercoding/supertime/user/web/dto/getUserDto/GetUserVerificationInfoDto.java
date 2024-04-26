package org.supercoding.supertime.user.web.dto.getUserDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetUserVerificationInfoDto {
    @Schema(description = "유저 고유 아이디")
    private Long userCid;

    @Schema(description = "유저 인증 상태")
    private Verified verified;

    @Schema(description = "유저 권한 정보")
    private Roles role;

    public static GetUserVerificationInfoDto from(UserEntity userEntity) {
        return GetUserVerificationInfoDto.builder()
                .userCid(userEntity.getUserCid())
                .verified(userEntity.getVerified())
                .role(userEntity.getRoles())
                .build();
    }

}
