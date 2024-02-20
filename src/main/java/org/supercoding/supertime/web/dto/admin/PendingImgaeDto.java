package org.supercoding.supertime.web.dto.admin;

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
public class PendingImgaeDto {
    @Schema(description = "인증 이미지 식별번호", example = "1")
    private Long authImageCid;

    @Schema(description = "인증 이미지 파일명", example = "example.img")
    private String authImageFileName;

    @Schema(description = "인증 이미지 파일URL", example = "www.이미지링크.com")
    private String authImageFilePath;
}
