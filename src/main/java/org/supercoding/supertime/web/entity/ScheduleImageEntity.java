package org.supercoding.supertime.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "schedule_table")
public class ScheduleImageEntity extends TimeEntity {
    @Id
    @Column(name = "schedule_image_cid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "시간표 이미지 식별번호")
    private Long scheduleCid;

    @Column(name = "week_number")
    @Schema(description = "부트캠프 주차", example = "1주차 or 기초주차")
    private Integer weekNumber;

    @Column(name = "schedule_image_file_name")
    @Schema(description = "시간표 이미지파일 이름", example = "시간표이미지.png")
    private String scheduleImageFileName;

    @Column(name = "schedule_image_file_path")
    @Schema(description = "시간표 이미지파일 URL", example = "www.시간표이미지.com")
    private String scheduleImageFilePath;
}
