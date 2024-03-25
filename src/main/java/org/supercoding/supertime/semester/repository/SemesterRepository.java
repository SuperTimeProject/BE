package org.supercoding.supertime.semester.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.semester.web.entity.SemesterEntity;

@Repository
public interface SemesterRepository extends JpaRepository<SemesterEntity, Long> {
    Boolean existsBySemesterName(int createSemesterName);
}
