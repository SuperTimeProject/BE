package org.supercoding.supertime.board.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class EditPostRequestDto {
    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;

    @Schema(description = "삭제 이미지 cid", example = "[1, 2, 4]")
    private List<Long> deleteImageList;
}
