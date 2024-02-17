package org.supercoding.supertime.web.dto.board.getUserPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetUserPostDto {

    @Schema(description = "게시물 식별번호", example = "1")
    private Long postCid;

    @Schema(description = "게시물 제목", example = "게시물 제목 예시")
    private String postTitle;

    @Schema(description = "작성일", example = "0000-00-00")
    private String createdAt;
}
