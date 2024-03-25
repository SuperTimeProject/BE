package org.supercoding.supertime.chat.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.user.entity.user.UserEntity;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoomDto {
    @Schema(description = "채팅방 고유번호", example = "2")
    private Long roomCid;

    @Schema(description = "채팅방 이름", example = "기수채팅방")
    private String roomName;

    @Schema(description = "채팅방 인원수", example = "10")
    private long userCount; // 채팅방 인원수

    public static ChatRoomDto from(final ChatRoomEntity chatRoom) {

        int userCount = 0;

        List<ChatRoomMemberEntity> chatRoomMembers = chatRoom.getChatRoomMemberList();
        if (chatRoomMembers != null && !chatRoomMembers.isEmpty()) {
            for (ChatRoomMemberEntity chatRoomMember : chatRoomMembers) {
                UserEntity chatMember = chatRoomMember.getUser();
                if (chatMember != null) {
                    userCount++;
                }
            }
        }

        return ChatRoomDto.builder()
                .roomCid(chatRoom.getChatRoomCid())
                .roomName(chatRoom.getChatRoomName())
                .userCount(userCount)
                .build();
    }

}
