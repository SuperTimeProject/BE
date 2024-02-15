package org.supercoding.supertime.comment.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.comment.web.entity.PostCommentEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetCommentDetailDto {

    @Schema(description = "작성자", example = "홍길동")
    private String author;

    @Schema(description = "댓글 내용", example = "이것이 댓글이다.")
    private String content;

    @Schema(description = "작성 시간")
    private LocalDateTime createdAt;

    public static GetCommentDetailDto from(final PostCommentEntity comment){
        return GetCommentDetailDto.builder()
                .author(comment.getUser().getUserNickname())
                .content(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
