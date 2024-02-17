package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.web.entity.schedule.ScheduleEntity;
import org.supercoding.supertime.web.entity.enums.IsFull;
import org.supercoding.supertime.web.entity.enums.Part;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    Optional<ScheduleEntity> findByPartAndIsFull(Part part, IsFull isFull);
}
