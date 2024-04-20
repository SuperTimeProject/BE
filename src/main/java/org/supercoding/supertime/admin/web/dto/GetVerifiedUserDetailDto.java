package org.supercoding.supertime.admin.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetVerifiedUserDetailDto {
    @Schema(description = "유저 아이디", example = "id123")
    private String userId;

    @Schema(description = "유저 Cid", example = "1")
    private Long userCid;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "인증 사진")
    private PendingImageDto image;

    @Schema(description = "유저 소속 기수")
    private Long semester;

    @Schema(description = "인증상태")
    private Verified verified;

    public static GetVerifiedUserDetailDto from(UserEntity user, PendingImageDto image, Verified verified) {
        return GetVerifiedUserDetailDto.builder()
                .userId(user.getUserId())
                .userNickname(user.getUserNickname())
                .semester(user.getSemester())
                .userName(user.getUserName())
                .image(image)
                .verified(verified)
                .build();
    }

    public static GetVerifiedUserDetailDto getVerifiedDetail(UserEntity user, PendingImageDto image) {
        return GetVerifiedUserDetailDto.builder()
                .userCid(user.getUserCid())
                .userId(user.getUserId())
                .userName(user.getUserName())
                .userNickname(user.getUserNickname())
                .image(image)
                .semester(user.getSemester())
                .verified(user.getVerified())
                .build();
    }

}
