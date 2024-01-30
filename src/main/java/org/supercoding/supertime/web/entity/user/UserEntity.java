package org.supercoding.supertime.web.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.supercoding.supertime.web.entity.TimeEntity;
import org.supercoding.supertime.web.entity.enums.Roles;

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
    @Column(name = "varified")
    @Schema(description = "사용자 인증 여부(0:비인증, 1:인증)", example = "1")
    private Integer varified;

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

    @Enumerated(EnumType.STRING)
    private Roles roles;

    @NotNull
    @Schema(description = "아이디 삭제 여부", example = "0 or 1으로 삭제여부 판단")
    @Column(name = "is_deleted")
    private int isDeleted;

}