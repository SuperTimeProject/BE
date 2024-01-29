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
@Table(name = "chatting_room_table")
public class ChattingRoomEntity extends TimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatting_room_cid")
    @Schema(description = "채팅방 식별번호", example = "1")
    private Long chattingRoomCid;

    @ManyToOne
    @JoinColumn(name = "user_cid",referencedColumnName = "user_cid", nullable = false)
    @Schema(description = "사용자 식별번호")
    private UserEntity user;

    @Column(name = "semester_name", length = 255)
    @Schema(description = "학기 이름")
    private String semesterName;
}
