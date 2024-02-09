package org.supercoding.supertime.chat.service;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.chat.dto.request.CreateChatMessageRequest;
import org.supercoding.supertime.chat.dto.response.ChatMessageResponseDto;
import org.supercoding.supertime.chat.entity.MessageType;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.chat.entity.ChatMessageEntity;
import org.supercoding.supertime.chat.entity.ChatRoomEntity;
import org.supercoding.supertime.web.entity.user.UserEntity;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResponseDto enterChatRoom(Long roomCid, CreateChatMessageRequest createChatMessageRequest, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(createChatMessageRequest.getContent())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage, MessageType.ENTER);
    }

    public ChatMessageResponseDto sendMessage(Long roomCid, CreateChatMessageRequest createChatMessageRequest, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        Boolean isRoomMember = chatRoom.getChatRoomMember().stream()
                .anyMatch(member -> member.getUserCid().equals(userEntity.getUserCid()));

        if(!isRoomMember){
            throw new AccessDeniedException("채팅방 맴버가 아닙니다");
        }

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(createChatMessageRequest.getContent())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage, MessageType.TALK);
    }
}
