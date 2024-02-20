package org.supercoding.supertime.chat.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.supercoding.supertime.chat.entity.MessageType;
import org.supercoding.supertime.chat.entity.ChatMessageEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDto {

    @Schema(description = "채팅 타입", example = "ENTER")
    private MessageType type;

    @Schema(description = "채팅 내용", example = "채팅 내용 예시")
    private String content;

    @Schema(description = "보낸사람", example = "사용자 A")
    private String sender;

    @Schema(description = "발송 시간")
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto from(final ChatMessageEntity chatMessage){
        return ChatMessageResponseDto.builder()
                .type(chatMessage.getType())
                .content(chatMessage.getChatMessageContent())
                .sender(chatMessage.getUser().getUserNickname())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

}
