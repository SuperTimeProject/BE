package org.supercoding.supertime.board.web.dto.getPostDetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.board.web.entity.PostImageEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class PostDetailImageDto {
    @Schema(description = "게시판 이미지 식별번호", example = "1")
    private Long postImageCid;

    @Schema(description = "게시판 이미지 파일명", example = "example.img")
    private String postImageFileName;

    @Schema(description = "게시판 이미지 파일URL", example = "www.이미지링크.com")
    private String postImageFilePath;

    public static PostDetailImageDto from(PostImageEntity image) {
        return PostDetailImageDto.builder()
                .postImageCid(image.getPostImageCid())
                .postImageFileName(image.getPostImageFileName())
                .postImageFilePath(image.getPostImageFilePath())
                .build();
    }
}
