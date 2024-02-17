package org.supercoding.supertime.web.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.supercoding.supertime.web.entity.board.BoardEntity;
import org.supercoding.supertime.web.entity.enums.Part;
import org.supercoding.supertime.web.entity.enums.Roles;
import org.supercoding.supertime.web.entity.enums.Valified;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
@Slf4j
public class UpdateUserInfoRequestDto {
    @Schema(description = "기수", example = "1")
    private Long semester;

    @Schema(description = "비밀번호", example = "1234")
    private String userPassword;

    @Schema(description = "유저 이름", example = "홍길동")
    private String userName;

    @Schema(description = "유저 닉네임", example = "피카츄")
    private String userNickname;

    @Enumerated(EnumType.STRING)
    private Valified valified;

    @Enumerated(EnumType.STRING)
    private Part part;

    @Enumerated(EnumType.STRING)
    private Roles roles;

    @Column(name = "is_deleted")
    private Integer isDeleted;
}
