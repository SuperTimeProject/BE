package org.supercoding.supertime.board.web.dto.getBoardPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.supercoding.supertime.board.web.entity.PostEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class BoardInfoDto {

    @Schema(description = "현재 페이지", example = "1")
    private int page;

    @Schema(description = "총 게시물 갯수", example = "13")
    private Long totalElements;

    @Schema(description = "총 페이지수", example = "2")
    private int totalPages;

    public static BoardInfoDto from(Page<PostEntity> postList, int page) {
        return BoardInfoDto.builder()
                .page(page)
                .totalElements(postList.getTotalElements())
                .totalPages(postList.getTotalPages())
                .build();
    }
}
