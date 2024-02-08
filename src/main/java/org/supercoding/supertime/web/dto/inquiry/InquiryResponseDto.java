package org.supercoding.supertime.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.dto.board.getPostDetail.PostDetailDto;
import org.supercoding.supertime.web.entity.enums.InquiryClosed;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class InquiryResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "문의 기록 리스트")
    private List<InquiryDetailDto> inquiryList;
}
