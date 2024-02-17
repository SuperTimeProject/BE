package org.supercoding.supertime.chat.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.supercoding.supertime.chat.entity.MessageType;
import org.supercoding.supertime.chat.entity.ChatMessageEntity;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponseDto {

    private MessageType type;
    private String content;
    private String sender;
    private Long roomCid;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto from(final ChatMessageEntity chatMessage){
        return ChatMessageResponseDto.builder()
                .type(chatMessage.getType())
                .content(chatMessage.getChatMessageContent())
                .sender(chatMessage.getUser().getUserNickname())
                .roomCid(chatMessage.getChatRoom().getChatRoomCid())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }

}
