package org.supercoding.supertime.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.chat.dto.ChatRoom;
import org.supercoding.supertime.chat.repository.ChatRepository;
import org.supercoding.supertime.chat.service.ChatRoomService;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
//    @GetMapping("/")
//    public ResponseEntity<List<ChatRoom>> goChatRoom(){
//        List<ChatRoom> chatRooms = chatRepository.findAllRoom();
//        return ResponseEntity.ok(chatRooms);
//    }

    // 채팅방 생성
    @PostMapping("/room")
    public ResponseEntity<CommonResponseDto> createRoom(
            @RequestParam String name
    ) {
        log.info("[CHAT_ROOM] 채팅방 생성 요청이 들어왔습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createRoom(name));
    }
    // fb1a3e90-64b0-4187-9cc1-4361a5f44252

    // 채팅에 참여한 유저 리스트 반환
//    @GetMapping("/userlist")
//    public ArrayList<String> userList(String roomId) {
//
//        return chatRepository.getUserList(roomId);
//    }
}
