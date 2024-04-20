package org.supercoding.supertime.chat.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_room_member_table")
public class ChatRoomMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_member_cid")
    @Schema(description = "채팅방 맴버 식별번호", example = "1")
    private Long ChatRoomMemberCid;

    @ManyToOne
    @JoinColumn(name = "chat_room_cid")
    private ChatRoomEntity chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_cid")
    private UserEntity user;

    public static ChatRoomMemberEntity from(final UserEntity user, final ChatRoomEntity chatRoom) {
        return ChatRoomMemberEntity.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();
    }
}
