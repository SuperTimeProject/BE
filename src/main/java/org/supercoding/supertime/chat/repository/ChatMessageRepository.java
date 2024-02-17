package org.supercoding.supertime.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.chat.entity.ChatMessageEntity;
import org.supercoding.supertime.chat.entity.ChatRoomEntity;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
    List<ChatMessageEntity> findAllByChatRoom(ChatRoomEntity chatRoom);
}
