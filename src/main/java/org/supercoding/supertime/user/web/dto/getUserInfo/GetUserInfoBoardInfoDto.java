package org.supercoding.supertime.user.web.dto.getUserInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.board.web.entity.BoardEntity;

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

    public static GetUserInfoBoardInfoDto from(BoardEntity board) {
        return GetUserInfoBoardInfoDto.builder()
                .boardCid(board.getBoardCid())
                .boardName(board.getBoardName())
                .build();
    }
}
