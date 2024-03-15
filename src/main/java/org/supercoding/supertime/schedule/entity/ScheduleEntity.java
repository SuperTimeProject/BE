package org.supercoding.supertime.schedule.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.supercoding.supertime.golbal.web.entity.TimeEntity;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "schedule_table")
public class ScheduleEntity extends TimeEntity {
    @Id
    @Column(name = "schedule_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "시간표 식별번호")
    private Long scheduleCid;

    @Column(name = "part")
    @Schema(description = "주특기", example = "PART_BE")
    private Part part;

    @Column(name = "isFull")
    @Schema(description = "풀타임 여부", example = "FULL")
    private IsFull isFull;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ScheduleImageEntity> imageList;
}
