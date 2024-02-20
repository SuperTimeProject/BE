package org.supercoding.supertime.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChatMessageRequest {

    @Schema(description = "유저 고유아이디", example = "1")
    private Long userCid;

    @Schema(description = "채팅 내용", example = "채팅 내용 입니다.")
    private String content;
}
