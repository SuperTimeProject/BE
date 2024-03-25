package org.supercoding.supertime.chat.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.supercoding.supertime.chat.web.dto.request.CreateChatRoomReqDto;
import org.supercoding.supertime.chat.web.dto.response.GetUserChatRoomResDto;
import org.supercoding.supertime.chat.web.dto.response.GetUserListResDto;
import org.supercoding.supertime.chat.service.ChatRoomService;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅 리스트 화면
    @GetMapping("/")
    @Operation(tags = {"채팅 API"}, summary = "채팅방 조회", description = "소속한 채팅방을 불러오는 api 입니다.")
    public ResponseEntity<GetUserChatRoomResDto> goChatRoom(
            @AuthenticationPrincipal User user
            ){
        log.info("[CHAT_ROOM] 채팅방 조회 요청이 들어왔습니다.");
        
        return ResponseEntity.ok(chatRoomService.findAllRoom(user));
    }

    // 채팅방 생성
    @Operation(tags = {"채팅 API"}, summary = "채팅방 생성", description = "채팅방을 생성하는 api 입니다.")
    @PostMapping("/room")
    public ResponseEntity<CommonResponseDto> createRoom(
            @RequestBody CreateChatRoomReqDto chatRoomReqDto
    ) {
        log.info("[CHAT_ROOM] 채팅방 생성 요청이 들어왔습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoomService.createRoom(chatRoomReqDto));
    }

    // 채팅에 참여한 유저 리스트 반환
    @Operation(tags = {"채팅 API"}, summary = "채팅참여 인원 조회", description = "채팅방 안에 유저들을 불러오는 api 입니다.")
    @GetMapping("/userlist/{roomCid}")
    public GetUserListResDto userList(
            @PathVariable Long roomCid,
            @AuthenticationPrincipal User user
    ) {

        return chatRoomService.getUserList(roomCid, user);
    }
}
