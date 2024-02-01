package org.supercoding.supertime.web.dto.board.getBoardPost;

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
public class GetBoardPostDetailDto {
    @Schema(description = "작성자")
    private String author;

    @Schema(description = "게시물 제목")
    private String postTitle;

    @Schema(description = "작성일", example = "23-11-10")
    private String createdAt;
}
