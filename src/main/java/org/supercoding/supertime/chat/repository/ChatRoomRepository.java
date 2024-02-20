package org.supercoding.supertime.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.chat.entity.ChatRoomEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Boolean existsByChatRoomName(String name);

    Optional<ChatRoomEntity> findByChatRoomName(String chatRoomName);
}
