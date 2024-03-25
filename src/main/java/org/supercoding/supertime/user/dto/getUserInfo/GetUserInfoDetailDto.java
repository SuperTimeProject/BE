package org.supercoding.supertime.user.dto.getUserInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.user.dto.getUserDto.UserProfileDto;
import org.supercoding.supertime.user.dto.getUserDto.UserSemesterDto;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Valified;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetUserInfoDetailDto {
    @Schema(description = "유저 고유 아이디")
    private Long userCid;

    @Schema(description = "유저 아이디", example = "id123")
    private String userId;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(description = "유저 선택 파트", example = "FE")
    private Part part;

    @Schema(description = "유저 권한", example = "ROLE_ADMIN")
    private Roles role;

    @Schema(description = "유저 소속 게시판", example = "[1,3,5]")
    private List<GetUserInfoBoardInfoDto> boardList;

    @Schema(description = "유저 소속 기수")
    private UserSemesterDto semester;

    @Schema(description = "유저 프로필")
    private UserProfileDto userProfile;

    @Schema(description = "인증 상태")
    private Valified valified;
}
