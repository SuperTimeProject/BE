package org.supercoding.supertime.schedule.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetAllSemesterResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "기수 리스트")
    private List<GetSemesterDto> semesterList;

    public static GetAllSemesterResponseDto successResponse(final String message, final List<GetSemesterDto> semesters) {
        return GetAllSemesterResponseDto.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .semesterList(semesters)
                .build();
    }
}
