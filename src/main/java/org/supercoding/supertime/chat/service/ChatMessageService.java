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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResponseDto enterChatRoom(Long roomCid, CreateChatMessageRequest message) {
        UserEntity userEntity = userRepository.findByUserId(message.getAuthor())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(MessageType.ENTER)
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(userEntity.getUserNickname()+"유저가 들어왔습니다.")
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
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
                .type(MessageType.TALK)
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(createChatMessageRequest.getContent())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }

    public ChatMessageResponseDto leaveChatRoom(Long roomCid, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(MessageType.LEAVE)
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(userEntity.getUserNickname()+"유저가 퇴장했습니다.")
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }

    public List<ChatMessageResponseDto> getAllMessage(Long roomCid, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        List<ChatMessageEntity> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);

        return chatMessageList.stream().map(ChatMessageResponseDto::from).toList();
    }
}
