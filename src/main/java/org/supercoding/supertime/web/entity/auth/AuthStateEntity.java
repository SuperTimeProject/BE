package org.supercoding.supertime.web.entity.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.web.entity.TimeEntity;
import org.supercoding.supertime.web.entity.enums.Valified;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "auth_table")
public class AuthStateEntity extends TimeEntity {
    @Id
    @Column(name = "auth_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "인증 현황 고유 아이디")
    private Long authCid;

    @Column(name = "auth_user_name")
    @Schema(description = "인증한 유저", example = "qwer1234@naver.com")
    private String userId;

    @Column(name = "valified")
    @Schema(description = "인증상태", example = "NEEDED")
    private Valified valified;

    @Column(name = "authImageId")
    @Schema(description = "인증 이미지 ID", example = "1")
    private Long authImageId;

}
