package org.supercoding.supertime.chat.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "chat_room_table")
public class ChatRoomEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_cid")
    @Schema(description = "채팅방 식별번호", example = "1")
    private Long chatRoomCid;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER)
    @Schema(description = "사용자 식별번호")
    private List<ChatRoomMemberEntity> chatRoomMemberList;

    @Schema(description = "채팅방 이름", example = "기수채팅방")
    private String chatRoomName;
}
