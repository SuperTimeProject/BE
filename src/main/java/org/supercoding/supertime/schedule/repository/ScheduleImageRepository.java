package org.supercoding.supertime.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.schedule.entity.ScheduleImageEntity;

import java.util.Optional;

public interface ScheduleImageRepository extends JpaRepository<ScheduleImageEntity, Long> {

    Optional<ScheduleImageEntity> findByScheduleIdAndWeekNumber(Long scheduleId,Integer weekNumber);
}
