package org.supercoding.supertime.web.dto.semester;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

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
}
