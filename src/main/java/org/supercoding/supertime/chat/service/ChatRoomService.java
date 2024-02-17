package org.supercoding.supertime.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.chat.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public CommonResponseDto createRoom(String name) {
        Boolean isExist = chatRoomRepository.existsByChatRoomName(name);

        if(isExist){
            throw new DataIntegrityViolationException("이미 동일한 이름의 채팅방이 존재합니다.");
        }

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(name)
                .build();

        chatRoomRepository.save(chatRoom);

        return CommonResponseDto.builder().build();
    }
}
