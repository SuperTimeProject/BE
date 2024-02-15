package org.supercoding.supertime.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.web.entity.enums.Valified;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);

    Boolean existsByUserId(String userName);

    Boolean existsByUserNickname(String userNickname);

    List<UserEntity> findAllByValified(Valified valified);
}
