package org.supercoding.supertime.board.web.dto.getUserPost;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.supercoding.supertime.board.web.dto.getBoardPost.BoardInfoDto;

import java.util.List;

@Getter
@Setter
@Builder
public class GetUserPostResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "유저 게시물 리스트")
    private List<GetUserPostDto> userPostList;

    private BoardInfoDto boardInfo;

    public static GetUserPostResponseDto success(String message, List<GetUserPostDto> userPosts, BoardInfoDto boardInfo){
        return GetUserPostResponseDto.builder()
                .code(200)
                .success(true)
                .message(message)
                .userPostList(userPosts)
                .boardInfo(boardInfo)
                .build();
    }
}
