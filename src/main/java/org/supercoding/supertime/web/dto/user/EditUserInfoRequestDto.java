package org.supercoding.supertime.web.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.dto.user.getUserDto.UserProfileDto;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class EditUserInfoRequestDto {
    @Schema(description = "유저 고유 아이디")
    private Long userCid;

    @Schema(description = "유저 닉네임", example = "또가스")
    private String userNickname;

    @Schema(description = "유저 프로필")
    private Long userProfileCid;

}
