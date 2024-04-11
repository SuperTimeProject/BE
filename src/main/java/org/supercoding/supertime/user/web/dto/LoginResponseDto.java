package org.supercoding.supertime.user.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class LoginResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "access-token")
    private String accessToken;

    public static LoginResponseDto successResponse(final String message, final String token) {
        return LoginResponseDto.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .accessToken(token)
                .build();
    }

}
