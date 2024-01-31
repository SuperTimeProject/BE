package org.supercoding.supertime.web.dto.board;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.board.PostImageEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class CreatePostRequestDto {
    // TODO - 시큐리티 구현시 user정보는 토큰기반으로 가져옴
    @Schema(description = "사용자 식별번호")
    private Long userCid;

    @Schema(description = "게시물 이미지 식별번호")
    private PostImageEntity postImageEntity;

    @Schema(description = "게시물 제목", example = "게시물 1")
    private String postTitle;

    @Schema(description = "게시물 내용", example = "1번 게시물 내용 예시")
    private String postContent;
}
