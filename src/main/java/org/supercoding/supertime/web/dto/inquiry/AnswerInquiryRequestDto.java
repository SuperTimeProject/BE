package org.supercoding.supertime.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import org.supercoding.supertime.web.entity.Inquiry.InquiryEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

public class AnswerInquiryRequestDto {
    @Schema(description = "문의 식별번호")
    private Long inquiryCid;

    @Schema(description = "문의 내용")
    private String inquiryContent;
}
