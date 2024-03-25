package org.supercoding.supertime.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.user.web.entity.AuthStateEntity;
import org.supercoding.supertime.golbal.web.enums.Valified;

import java.util.Optional;
@Repository
public interface AuthStateRepository extends JpaRepository<AuthStateEntity,Long> {
    Optional<AuthStateEntity> findByUserId(String userId);
    Optional<AuthStateEntity> findByValified(Valified valified);
}
