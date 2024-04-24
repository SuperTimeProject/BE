package org.supercoding.supertime.admin.web.dto.verified;

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
public class GetVerifiedUserDetailResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    private GetVerifiedUserDetailDto userDetail;

    public static GetVerifiedUserDetailResponseDto successResponse(String message, GetVerifiedUserDetailDto userDetail) {
        return GetVerifiedUserDetailResponseDto.builder()
                .success(true)
                .code(200)
                .message(message)
                .userDetail(userDetail)
                .build();
    }

}
