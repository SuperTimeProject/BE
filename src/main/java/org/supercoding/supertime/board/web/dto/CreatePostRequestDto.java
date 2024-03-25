package org.supercoding.supertime.board.web.dto;

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
public class CreatePostRequestDto {

    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;
}
