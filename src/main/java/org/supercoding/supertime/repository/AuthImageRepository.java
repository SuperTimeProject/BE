package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.auth.AuthImageEntity;

@Repository
public interface AuthImageRepository extends JpaRepository<AuthImageEntity, Long> {

}
