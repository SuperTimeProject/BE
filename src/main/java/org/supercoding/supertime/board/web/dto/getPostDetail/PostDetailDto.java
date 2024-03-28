package org.supercoding.supertime.board.web.dto.getPostDetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.board.web.entity.PostEntity;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class PostDetailDto {
    @Schema(description = "게시물 식별번호", example = "1")
    private Long postCid;

    @Schema(description = "작성자")
    private String author;

    @Schema(description = "게시물 이미지 리스트")
    private List<PostDetailImageDto> imageList;

    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;

    @Schema(description = "작성일", example = "23-11-10")
    private String createdAt;

    @Schema(description = "게시물 조회수", example = "39")
    private int postView;

    public static PostDetailDto from(PostEntity post, List<PostDetailImageDto> imageList, String simpleDate) {
        return PostDetailDto.builder()
                .postCid(post.getPostCid())
                .author(post.getUserEntity().getUserNickname()) // 게시판에서는 닉네임을 사용
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .imageList(imageList)
                .postView(post.getPostView())
                .createdAt(simpleDate)
                .build();
    }
}
