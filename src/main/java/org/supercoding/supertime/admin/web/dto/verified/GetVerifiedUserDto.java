package org.supercoding.supertime.admin.web.dto.verified;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetVerifiedUserDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "승인 대기중인 유저 리스트")
    private List<GetVerifiedUserDetailDto> userList;

    public static GetVerifiedUserDto successResponse(final String message, List<GetVerifiedUserDetailDto> userList) {
        return GetVerifiedUserDto.builder()
                .success(true)
                .code(200)
                .message(message)
                .userList(userList)
                .build();
    }
}
