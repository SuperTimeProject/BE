package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.web.entity.ScheduleImageEntity;

public interface ScheduleImageRepository extends JpaRepository<ScheduleImageEntity, Long> {
}
