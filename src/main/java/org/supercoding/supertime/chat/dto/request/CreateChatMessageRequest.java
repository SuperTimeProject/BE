package org.supercoding.supertime.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateChatMessageRequest {

    private String content;
}
