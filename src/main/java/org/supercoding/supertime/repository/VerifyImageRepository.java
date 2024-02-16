package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.auth.VerifyImageEntity;

@Repository
public interface VerifyImageRepository extends JpaRepository<VerifyImageEntity, Long> {
}
