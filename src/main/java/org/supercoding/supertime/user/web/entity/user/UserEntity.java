package org.supercoding.supertime.user.web.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;
import org.supercoding.supertime.board.web.entity.BoardEntity;
import org.supercoding.supertime.golbal.web.enums.Part;
import org.supercoding.supertime.golbal.web.enums.Roles;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.user.web.dto.SignupRequestDto;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_table")
public class UserEntity extends TimeEntity {
    @Id
    @Column(name = "user_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "유저 고유 아이디")
    private Long userCid;

    @NotNull
    @Column(name = "semester")
    @Schema(description = "기수", example = "1")
    private Long semester;

    @Column(name = "user_profile_cid")
    @Schema(description = "유저 프로필 사진", example = "1")
    private Long userProfileCid;

    @NotNull
    @Column(name = "user_id")
    @Schema(description = "유저 아이디", example = "id123")
    private String userId;

    @NotNull
    @Column(name = "user_password")
    @Schema(description = "비밀번호", example = "1234")
    private String userPassword;

    @NotNull
    @Column(name = "user_name")
    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @NotNull
    @Column(name = "user_nickname")
    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Schema(name = "게시판 리스트")
    private long[] boardList;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Column(name = "chat_room_list")
    private List<ChatRoomMemberEntity> chatRoomMemberList;

    @Enumerated(EnumType.STRING)
    private Verified verified;

    @Enumerated(EnumType.STRING)
    private Part part;

    @Enumerated(EnumType.STRING)
    private Roles roles;

    @NotNull
    @Schema(description = "아이디 삭제 여부", example = "0 or 1으로 삭제여부 판단")
    @Column(name = "is_deleted")
    private int isDeleted;

    public static UserEntity from(final SignupRequestDto signupInfo, final String password, final long[] userBoard) {
        return  UserEntity.builder()
                .userId(signupInfo.getUserId())
                .userName(signupInfo.getUserName())
                .userNickname(signupInfo.getUserNickname())
                .userPassword(password)
                .semester(signupInfo.getSemesterCid())
                .boardList(userBoard)
                .roles(Roles.ROLE_USER)
                .part(Part.PART_UNDEFINED)
                .isDeleted(0)
                .verified(Verified.NEEDED)
                .build();
    }

    public UserEntity update(String name){
        this.userName = name;
        return this;
    }
}
