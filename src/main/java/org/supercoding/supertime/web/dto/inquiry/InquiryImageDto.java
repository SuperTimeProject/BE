package org.supercoding.supertime.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class InquiryImageDto {
    @Schema(description = "문의하기 이미지 식별번호", example = "1")
    private Long postImageCid;

    @Schema(description = "문의하기 이미지 파일명", example = "example.img")
    private String postImageFileName;

    @Schema(description = "문의하기 이미지 파일URL", example = "www.이미지링크.com")
    private String postImageFilePath;
}
