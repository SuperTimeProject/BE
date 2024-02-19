package org.supercoding.supertime.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class GetUnclosedInquiryDetailDto {
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

}
