package org.supercoding.supertime.admin.web.dto.inquiry;

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
public class GetInquiryRes {
    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = " 문의 리스트")
    private List<GetInquiryDetail> inquiryList;

    public static GetInquiryRes from(String message, List<GetInquiryDetail> inquiryList) {
        return GetInquiryRes.builder()
                .success(true)
                .code(200)
                .message(message)
                .inquiryList(inquiryList)
                .build();
    }
}
