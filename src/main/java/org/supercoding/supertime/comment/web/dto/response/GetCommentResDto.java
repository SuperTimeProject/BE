package org.supercoding.supertime.comment.web.dto.response;

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
public class GetCommentResDto {

    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "댓글 리스트")
    private List<GetCommentDetailDto> commentList;

    public static GetCommentResDto success(final List<GetCommentDetailDto> commentList){
        return GetCommentResDto.builder()
                .success(true)
                .code(HttpStatus.OK.value())
                .message("성공적으로 댓글 리스트를 불러왔습니다.")
                .commentList(commentList)
                .build();
    }
}
