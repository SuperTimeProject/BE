package org.supercoding.supertime.semester.web.dto;

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
public class ScheduleImageDto {
    @Schema(description = "시간표 이미지 식별번호", example = "1")
    private Long scheduleImageCid;

    @Schema(description = "시간표 이미지 파일명", example = "example.img")
    private String scheduleImageFileName;

    @Schema(description = "시간표 이미지 파일URL", example = "www.이미지링크.com")
    private String scheduleImageFilePath;
}
