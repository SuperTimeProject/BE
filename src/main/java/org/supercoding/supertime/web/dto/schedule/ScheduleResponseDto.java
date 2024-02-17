package org.supercoding.supertime.web.dto.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import org.supercoding.supertime.web.dto.board.getUserPost.GetUserPostDto;

import java.util.List;

public class ScheduleResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "유저 게시물 리스트")
    private Long L;

}
