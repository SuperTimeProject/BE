package org.supercoding.supertime.chat.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.web.entity.TimeEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_message_table")
public class ChatMessageEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_cid")
    @Schema(description = "메시지 식별번호", example = "1")
    private Long chatMessageCid;

    @ManyToOne
    @JoinColumn(name = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "chat_room_cid", nullable = false)
    @Schema(description = "채팅방 식별번호")
    private ChatRoomEntity chatRoom;

    @Column(name = "chat_message_content", columnDefinition = "TEXT")
    @Schema(description = "채팅 내용", example = "안녕?")
    private String chatMessageContent;


}
