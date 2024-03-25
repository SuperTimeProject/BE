package org.supercoding.supertime.board.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "post_image_table")
public class PostImageEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_image_cid")
    @Schema(description = "게시판 이미지 식별번호", example = "1")
    private Long postImageCid;

    @Column(name = "post_image_file_name")
    @Schema(description = "게시판 이미지 파일명", example = "example.img")
    private String postImageFileName;

    @Column(name = "post_image_file_path")
    @Schema(description = "게시판 이미지 파일URL", example = "www.이미지링크.com")
    private String postImageFilePath;
}
