package org.supercoding.supertime.schedule.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetSemesterDto {
    @Schema(description = "기수 고유 아이디")
    private Long semesterCid;

    @Schema(description = "기수", example = "2311")
    private String semesterDetailName;

    public static GetSemesterDto from(SemesterEntity semester) {
        return GetSemesterDto.builder()
                .semesterCid(semester.getSemesterCid())
                .semesterDetailName(semester.getSemesterDetailName())
                .build();
    }
}
