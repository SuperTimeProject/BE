package org.supercoding.supertime.web.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.board.PostImageEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class EditPostRequestDto {

    @Schema(description = "게시물 이미지 식별번호")
    private Long postImageCid;

    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;
}
