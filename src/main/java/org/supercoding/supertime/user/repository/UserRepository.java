package org.supercoding.supertime.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.golbal.web.enums.Verified;
import org.supercoding.supertime.user.web.entity.user.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);

    Optional<UserEntity> findByUserCid(Long userCid);

    Boolean existsByUserId(String userName);

    Boolean existsByUserNickname(String userNickname);

    Page<UserEntity> findAllByValified(Verified verified, Pageable pageable);
}
