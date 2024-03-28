package org.supercoding.supertime.board.web.dto.getBoardPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.board.web.entity.PostEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetBoardPostDetailDto {

    @Schema(description = "포스트 cid")
    private Long postCid;

    @Schema(description = "작성자")
    private String author;

    @Schema(description = "게시물 제목")
    private String postTitle;

    @Schema(description = "게시물 조회수", example = "39")
    private int postView;

    @Schema(description = "작성일", example = "23-11-10")
    private String createdAt;

    public static GetBoardPostDetailDto from(PostEntity post, String simpleDate) {
        return GetBoardPostDetailDto.builder()
                .author(post.getUserEntity().getUserNickname())
                .postCid(post.getPostCid())
                .postTitle(post.getPostTitle())
                .postView(post.getPostView())
                .createdAt(simpleDate)
                .build();
    }
}
