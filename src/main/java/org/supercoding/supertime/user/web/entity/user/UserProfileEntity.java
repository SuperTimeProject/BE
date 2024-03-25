package org.supercoding.supertime.user.web.entity.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_profile_table")
public class UserProfileEntity extends TimeEntity {
    @Id
    @Column(name = "user_profile_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "유저 프로필 이미지 고유 아이디")
    private Long userProfileCid;

    @NotNull
    @Column(name = "user_profile_file_name")
    @Schema(description = "유저 프로필 이미지파일 이름")
    private String userProfileFileName;

    @NotNull
    @Column(name = "user_profile_file_path")
    @Schema(description = "유저 프로필 이미지파일 경로")
    private String userProfileFilePath;

}
