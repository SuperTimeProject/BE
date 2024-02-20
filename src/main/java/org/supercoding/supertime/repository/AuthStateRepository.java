package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.auth.AuthStateEntity;
import org.supercoding.supertime.web.entity.enums.Valified;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.Optional;
@Repository
public interface AuthStateRepository extends JpaRepository<AuthStateEntity,Long> {
    Optional<AuthStateEntity> findByUserId(String userId);
    Optional<AuthStateEntity> findByValified(Valified valified);
}
