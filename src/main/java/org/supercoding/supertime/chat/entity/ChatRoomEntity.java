package org.supercoding.supertime.chat.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.web.entity.TimeEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

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

    @ManyToMany
    @JoinTable(
            name = "room_member",
            joinColumns = @JoinColumn(name = "chat_room_cid"),
            inverseJoinColumns = @JoinColumn(name = "user_cid")
    )
    @Column(name = "chat_room_member")
    @Schema(description = "사용자 식별번호")
    private List<UserEntity> chatRoomMember;

    private String chatRoomName;
}
