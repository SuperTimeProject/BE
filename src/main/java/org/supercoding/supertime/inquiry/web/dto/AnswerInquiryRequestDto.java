package org.supercoding.supertime.inquiry.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class AnswerInquiryRequestDto {
    @Schema(description = "문의 식별번호")
    private Long inquiryCid;

    @Schema(description = "문의 내용")
    private String inquiryContent;
}
