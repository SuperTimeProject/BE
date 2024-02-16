package org.supercoding.supertime.web.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.web.entity.TimeEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "verify_image_table")
public class VerifyImageEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verify_image_cid")
    @Schema(description = "인증 이미지 식별번호", example = "1")
    private Long inquiryImageCid;

    @Column(name = "verify_image_file_name")
    @Schema(description = "인증 이미지 파일명", example = "example.img")
    private String inquiryImageFileName;

    @Column(name = "verify_image_file_path")
    @Schema(description = "인증 이미지 파일URL", example = "www.이미지링크.com")
    private String inquiryImageFilePath;
}
