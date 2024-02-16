package org.supercoding.supertime.web.dto.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class InquiryRequestDto {
    @Schema(description = "문의 제목", example = "문의 제목 예시")
    private String inquiryTitle;

    @Schema(description = "문의 내용")
    private String inquiryContent;


}
