package org.supercoding.supertime.schedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.supercoding.supertime.schedule.web.entity.ScheduleEntity;
import org.supercoding.supertime.golbal.web.enums.IsFull;
import org.supercoding.supertime.golbal.web.enums.Part;

import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    Optional<ScheduleEntity> findByPartAndIsFull(Part part, IsFull isFull);
}
