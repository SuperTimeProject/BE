package org.supercoding.supertime.chat.service;

import com.amazonaws.services.kms.model.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.chat.dto.request.CreateChatMessageRequest;
import org.supercoding.supertime.chat.dto.response.ChatMessageResponseDto;
import org.supercoding.supertime.chat.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.chat.entity.MessageType;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomMemberRepository;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.repository.UserRepository;
import org.supercoding.supertime.chat.entity.ChatMessageEntity;
import org.supercoding.supertime.chat.entity.ChatRoomEntity;
import org.supercoding.supertime.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.web.entity.user.UserEntity;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatMessageResponseDto enterChatRoom(Long roomCid, CreateChatMessageRequest message) {
        UserEntity userEntity = userRepository.findById(message.getUserCid())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        if (userEntity.getChatRoomMemberList().stream()
                .anyMatch(member -> member.getChatRoom().getChatRoomCid().equals(chatRoom.getChatRoomCid()))) {
            throw new CustomAccessDeniedException("이미 채팅방 맴버입니다.");
        }

        ChatRoomMemberEntity chatRoomMember = ChatRoomMemberEntity.builder()
                .user(userEntity)
                .chatRoom(chatRoom)
                .build();
        chatRoomMemberRepository.save(chatRoomMember);

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(MessageType.ENTER)
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(userEntity.getUserNickname()+"유저가 들어왔습니다.")
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }

    public ChatMessageResponseDto sendMessage(Long roomCid, CreateChatMessageRequest message) {
        UserEntity userEntity = userRepository.findById(message.getUserCid())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        if (!userEntity.getChatRoomMemberList().stream()
                .anyMatch(member -> member.getChatRoom().getChatRoomCid().equals(chatRoom.getChatRoomCid()))) {
            throw new CustomAccessDeniedException("채팅방 맴버가 아닙니다.");
        }

        ChatMessageEntity chatMessage = ChatMessageEntity.builder()
                .type(MessageType.TALK)
                .user(userEntity)
                .chatRoom(chatRoom)
                .chatMessageContent(message.getContent())
                .build();
        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDto.from(chatMessage);
    }

    @Transactional
    public ChatMessageResponseDto leaveChatRoom(Long roomCid, CreateChatMessageRequest message) {
        UserEntity userEntity = userRepository.findById(message.getUserCid())
                .orElseThrow(()-> new NotFoundException("유저가 존재하지 않습니다."));
        ChatRoomEntity chatRoom = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new NotFoundException("채팅방이 존재하지 않습니다."));

        if (!userEntity.getChatRoomMemberList().stream()
                .anyMatch(member -> member.getChatRoom().getChatRoomCid().equals(chatRoom.getChatRoomCid()))) {
            throw new CustomAccessDeniedException("채팅방 맴버가 아닙니다.");
        }

        chatRoomMemberRepository.deleteByChatRoomAndUser(chatRoom, userEntity);

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

        if (!userEntity.getChatRoomMemberList().stream()
                .anyMatch(member -> member.getChatRoom().getChatRoomCid().equals(chatRoom.getChatRoomCid()))) {
            throw new CustomAccessDeniedException("채팅방 맴버가 아닙니다.");
        }

        List<ChatMessageEntity> chatMessageList = chatMessageRepository.findAllByChatRoom(chatRoom);

        return chatMessageList.stream().map(ChatMessageResponseDto::from)
                .sorted(Comparator.comparing(ChatMessageResponseDto::getCreatedAt))
                .collect(Collectors.toList());
    }
}
