package org.supercoding.supertime.user.web.dto.getUserDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class UserSemesterDto {
    @Schema(description = "기수 고유 아이디")
    private Long semesterCid;

    @Schema(description = "기수", example = "2311")
    private String semesterDetailName;

    @Schema(description = "풀 or 하프")
    private IsFull isFull;

    public static UserSemesterDto from(SemesterEntity semester) {
        return UserSemesterDto.builder()
                .semesterCid(semester.getSemesterCid())
                .semesterDetailName(semester.getSemesterDetailName())
                .isFull(semester.getIsFull())
                .build();
    }
}
