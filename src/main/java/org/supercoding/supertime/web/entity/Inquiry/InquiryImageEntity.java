package org.supercoding.supertime.web.entity.Inquiry;

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
@Table(name = "inquiry_image_table")
public class InquiryImageEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_image_cid")
    @Schema(description = "문의하기 이미지 식별번호", example = "1")
    private Long inquiryImageCid;

    @Column(name = "inquiry_image_file_name")
    @Schema(description = "문의하기 이미지 파일명", example = "example.img")
    private String inquiryImageFileName;

    @Column(name = "inquiry_image_file_path")
    @Schema(description = "문의하기 이미지 파일URL", example = "www.이미지링크.com")
    private String inquiryImageFilePath;
}
