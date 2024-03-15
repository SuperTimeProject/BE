package org.supercoding.supertime.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.user.entity.user.UserEntity;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMemberEntity, Long> {
    void deleteByChatRoomAndUser(ChatRoomEntity chatRoom, UserEntity userEntity);
}
