package org.supercoding.supertime.chat.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChatRoomReqDto {
    
    @Schema(description = "채팅방 이름", example = "기수 채팅방")
    private String roomName;
}
