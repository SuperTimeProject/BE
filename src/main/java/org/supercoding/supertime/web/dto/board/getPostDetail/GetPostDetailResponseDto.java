package org.supercoding.supertime.web.dto.board.getPostDetail;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailDto;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetPostDetailResponseDto {
    private Boolean success;
    private int code;
    private String message;
    private PostDetailDto postInfo;
    // 댓글에 대한 리스트를 어디서 불러올지 정하기
}
