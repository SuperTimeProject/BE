package org.supercoding.supertime.user.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "auth_image_table")
public class AuthImageEntity {
    @Id
    @Column(name = "auth_image_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "인증 이미지 식별번호")
    private Long AuthImageCid;

    @Column(name = "auth_image_file_name")
    @Schema(description = "인증 이미지파일 이름", example = "인증이미지.png")
    private String AuthImageFileName;

    @Column(name = "auth_image_file_path")
    @Schema(description = "인증 이미지파일 URL", example = "www.인증이미지.com")
    private String AuthImageFilePath;

}
