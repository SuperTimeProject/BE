package org.supercoding.supertime.web.dto.user.getUserInfo;

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
public class GetUserInfoBoardInfoDto {
    @Schema(description = "게시판 이름", example = "자유 게시판")
    private String boardName;

    @Schema(description = "게시판 고유번호", example = "1")
    private Long boardCid;
}
