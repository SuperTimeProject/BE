package org.supercoding.supertime.web.dto.user.getUserDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.enums.IsFull;

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
}
