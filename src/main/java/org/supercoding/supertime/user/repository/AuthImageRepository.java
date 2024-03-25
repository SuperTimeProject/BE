package org.supercoding.supertime.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.user.web.entity.AuthImageEntity;

@Repository
public interface AuthImageRepository extends JpaRepository<AuthImageEntity, Long> {

}
