package org.supercoding.supertime.golbal.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class CommonResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    public static CommonResponseDto successResponse(final String message){
        return CommonResponseDto.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .build();
    }

    public static CommonResponseDto createSuccessResponse(final String message){
        return CommonResponseDto.builder()
                .code(HttpStatus.CREATED.value())
                .success(true)
                .message(message)
                .build();
    }
}
