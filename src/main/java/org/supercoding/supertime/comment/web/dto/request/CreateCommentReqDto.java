package org.supercoding.supertime.comment.web.dto.request;

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
public class CreateCommentReqDto {

    @Schema(description = "게시물 cid", example = "2")
    private Long postCid;
    
    @Schema(description = "댓글 내용", example = "댓글 내용 예시")
    private String content;
}
