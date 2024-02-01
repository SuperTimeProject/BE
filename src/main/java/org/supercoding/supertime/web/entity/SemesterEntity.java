package org.supercoding.supertime.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;
import org.supercoding.supertime.web.entity.enums.Roles;

import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "semester_table")
public class SemesterEntity {

    @Id
    @Column(name = "semester_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "기수 고유 아이디")
    private Long semesterCid;

    @NotNull
    @Column(name = "semester_name")
    @Schema(description = "기수", example = "2311")
    private Integer semesterName;

    @NotNull
    @Column(name = "semester_detail_name")
    @Schema(description = "기수", example = "2311")
    private String semesterDetailName;

    @NotNull
    @Column(name = "start_date")
    @Schema(description = "과정 시작일", example = "2023-11-11")
    private Date startDate;

    @Enumerated(EnumType.STRING)
    private Part part;


}
