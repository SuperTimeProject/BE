package org.supercoding.supertime.web.dto.board.getBoardPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.supercoding.supertime.web.dto.board.getPostDetail.GetPostDetailResponseDto;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetBoardPostResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "게시물 리스트")
    private List<GetBoardPostDetailDto> postList;

    public static GetBoardPostResponseDto successResponse(final String message, final List<GetBoardPostDetailDto> postList) {
        return GetBoardPostResponseDto.builder()
                .code(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .postList(postList)
                .build();
    }
}
