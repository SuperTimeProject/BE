package org.supercoding.supertime.web.dto.user.getUserInfo;

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
public class GetUserInfoResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "유저 정보")
    private GetUserInfoDetailDto getUserInfo;

    public static GetUserInfoResponseDto successResponse(final String message, final GetUserInfoDetailDto userInfo) {
        return GetUserInfoResponseDto.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .getUserInfo(userInfo)
                .build();
    }
}