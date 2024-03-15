package org.supercoding.supertime.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.supercoding.supertime.chat.web.dto.ChatRoomDto;
import org.supercoding.supertime.chat.web.dto.request.CreateChatRoomReqDto;
import org.supercoding.supertime.chat.web.dto.response.GetUserChatRoomResDto;
import org.supercoding.supertime.chat.web.dto.response.GetUserListResDto;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.chat.repository.ChatMessageRepository;
import org.supercoding.supertime.chat.repository.ChatRoomRepository;
import org.supercoding.supertime.user.repository.UserRepository;
import org.supercoding.supertime.golbal.web.advice.CustomAccessDeniedException;
import org.supercoding.supertime.golbal.web.advice.CustomNoSuchElementException;
import org.supercoding.supertime.golbal.web.advice.CustomNotFoundException;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;
import org.supercoding.supertime.user.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public CommonResponseDto createRoom(CreateChatRoomReqDto chatRoomReqDto) {
        Boolean isExist = chatRoomRepository.existsByChatRoomName(chatRoomReqDto.getRoomName());

        if(isExist){
            throw new DataIntegrityViolationException("이미 동일한 이름의 채팅방이 존재합니다.");
        }

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(chatRoomReqDto.getRoomName())
                .build();

        chatRoomRepository.save(chatRoom);

        return CommonResponseDto.builder().build();
    }
    public void createDefaultRoom(String semesterName) {
        Boolean isExist = chatRoomRepository.existsByChatRoomName(semesterName);

        if(isExist){
            throw new DataIntegrityViolationException("이미 동일한 이름의 채팅방이 존재합니다.");
        }

        ChatRoomEntity chatRoom = ChatRoomEntity.builder()
                .chatRoomName(semesterName)
                .build();

        chatRoomRepository.save(chatRoom);
    }

    public GetUserChatRoomResDto findAllRoom(User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 유저가 없습니다."));

        List<ChatRoomEntity> chatRoomEntityList = userEntity.getChatRoomMemberList().stream()
                .map(ChatRoomMemberEntity::getChatRoom)
                .collect(Collectors.toList());

        if(chatRoomEntityList.isEmpty()) {
            throw new CustomNoSuchElementException("참여중인 채팅창이 없습니다");
        }

        List<ChatRoomDto> userRoom = new ArrayList<>();
        for(ChatRoomEntity chatRoomEntity : chatRoomEntityList) {
            userRoom.add(ChatRoomDto.from(chatRoomEntity));
        }

        return GetUserChatRoomResDto.successResult("성공적으로 채팅방을 불러왔습니다.", userRoom);
    }

    public GetUserListResDto getUserList(Long roomCid, User user) {
        UserEntity userEntity = userRepository.findByUserId(user.getUsername())
                .orElseThrow(() -> new CustomNotFoundException("일치하는 유저가 없습니다."));

        ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(roomCid)
                .orElseThrow(()-> new CustomNotFoundException("일치하는 채팅방이 없습니다."));

        if (!userEntity.getChatRoomMemberList().stream()
                .anyMatch(member -> member.getChatRoom().getChatRoomCid().equals(chatRoomEntity.getChatRoomCid()))) {
            throw new CustomAccessDeniedException("채팅방 맴버가 아닙니다.");
        }

        return GetUserListResDto.successResult("성공적으로 유저 리스트를 불러왔습니다.", chatRoomEntity);
    }
}


























