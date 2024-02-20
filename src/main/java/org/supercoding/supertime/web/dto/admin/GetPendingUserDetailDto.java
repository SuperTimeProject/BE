package org.supercoding.supertime.web.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.enums.Valified;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetPendingUserDetailDto {
    @Schema(description = "유저 아이디", example = "id123")
    private String userId;

    @Schema(description = "유저 Cid", example = "1")
    private Long userCid;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "인증 사진")
    private PendingImgaeDto image;

    @Schema(description = "유저 소속 기수")
    private Long semester;

    @Schema(description = "인증상태")
    private Valified valified;

}
