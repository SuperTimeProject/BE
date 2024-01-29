package org.supercoding.supertime.web.entity.chatting;

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
@Table(name = "chatting_message_table")
public class ChattingMessageEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_message_cid")
    @Schema(description = "채팅방 식별번호", example = "1")
    private Long chattingMessageCid;

    @ManyToOne
    @JoinColumn(name = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "chatting_room_cid", nullable = false)
    @Schema(description = "채팅방 식별번호")
    private ChattingRoomEntity chattingRoom;

    @Column(name = "chatting_message_content", columnDefinition = "TEXT")
    @Schema(description = "채팅 내용", example = "안녕?")
    private String chattingMessageContent;

}
