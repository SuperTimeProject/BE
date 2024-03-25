package org.supercoding.supertime.inquiry.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.golbal.web.enums.InquiryClosed;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class InquiryDetailDto {
    @Schema(description = "문의 식별번호", example = "1")
    private Long inquiryCid;

    @Schema(description = "사용자 식별번호")
    private String userId;

    @Schema(description = "문의 제목", example = "문의 제목 예시")
    private String inquiryTitle;

    @Schema(description = "문의 내용")
    private String inquiryContent;

    @Schema(description = "문의하기 이미지 리스트")
    private List<InquiryImageDto> imageList;

    @Schema(description = "답변 내용")
    private String answer;

    @Schema(description = "문의 닫힘 여부 (1 or 0)", example = "1")
    private InquiryClosed isClosed;

}
