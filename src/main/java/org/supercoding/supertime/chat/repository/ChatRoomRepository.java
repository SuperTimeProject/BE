package org.supercoding.supertime.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Long> {
    Boolean existsByChatRoomName(String name);

    Optional<ChatRoomEntity> findByChatRoomName(String chatRoomName);
}
