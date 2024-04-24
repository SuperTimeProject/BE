package org.supercoding.supertime.admin.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.inquiry.web.entity.InquiryEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetInquiryDetail {
    @Schema(description = "문의 cid")
    private Long inquiryCid;

    @Schema(description = "작성자")
    private String author;

    @Schema(description = "문의 제목")
    private String inquiryTitle;

    @Schema(description = "문의 내용")
    private String inquiryContent;

    @Schema(description = "답변 내용")
    private String answer;

    @Schema(description = "작성일", example = "23-11-10")
    private String createdAt;

    public static GetInquiryDetail from(InquiryEntity inquiryEntity) {
        return GetInquiryDetail.builder()
                .inquiryCid(inquiryEntity.getInquiryCid())
                .author(inquiryEntity.getUser().getUserId())
                .inquiryTitle(inquiryEntity.getInquiryTitle())
                .inquiryContent(inquiryEntity.getInquiryContent())
                .createdAt(inquiryEntity.getCreatedAt().toString())
                .answer(inquiryEntity.getAnswer())
                .build();
    }
}
