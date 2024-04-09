package org.supercoding.supertime.inquiry.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class InquiryDetailResponseDto {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    private InquiryDetailDto inquiryInfo;

    public static InquiryDetailResponseDto from(String message, InquiryDetailDto inquiryInfo) {
        return InquiryDetailResponseDto.builder()
                .success(true)
                .code(200)
                .message(message)
                .inquiryInfo(inquiryInfo)
                .build();
    }
}
