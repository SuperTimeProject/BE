package org.supercoding.supertime.user.dto.getUserDto;

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
public class UserProfileDto {
    @Schema(description = "유저 프로필 이미지 고유 아이디")
    private Long userProfileCid;

    @Schema(description = "유저 프로필 이미지파일 이름")
    private String userProfileFileName;

    @Schema(description = "유저 프로필 이미지파일 경로")
    private String userProfileFilePath;
}
