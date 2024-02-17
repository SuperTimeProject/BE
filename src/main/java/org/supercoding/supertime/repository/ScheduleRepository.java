package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.web.entity.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
}
