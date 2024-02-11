package org.supercoding.supertime.web.dto.board.getPostDetail;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailDto;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetPostDetailResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "게시물 상세 내용")
    private PostDetailDto postInfo;
    // 댓글에 대한 리스트를 어디서 불러올지 정하기

    public static GetPostDetailResponseDto successResponse(final String message, final PostDetailDto post) {
        return GetPostDetailResponseDto.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message(message)
                .postInfo(post)
                .build();
    }
}
