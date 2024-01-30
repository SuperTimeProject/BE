package org.supercoding.supertime.web.dto.semester;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class CreateSemesterRequestDto {
    @Schema(description = "생성 기수", example = "2311")
    private int semesterName;

    @Schema(description = "기수 시작일", example = "23-11-23")
    private Date startDate;
}
