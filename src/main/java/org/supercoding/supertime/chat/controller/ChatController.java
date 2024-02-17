package org.supercoding.supertime.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.supercoding.supertime.chat.dto.request.CreateChatMessageRequest;
import org.supercoding.supertime.chat.dto.response.ChatMessageResponseDto;
import org.supercoding.supertime.chat.service.ChatMessageService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;

    @MessageMapping("/messages/enter/{roomCid}")
    @SendTo("/receive/rooms/{roomCid}")
    public ChatMessageResponseDto enterChatRoom(
            @DestinationVariable Long roomCid,
            @Payload CreateChatMessageRequest message
//            @AuthenticationPrincipal User user
            ){
        log.info("[ENTER_CHAT_ROOM] 채팅창 입장 요청이 들어왔습니다.");
        return chatMessageService.enterChatRoom(roomCid, message);
    }

    @MessageMapping("/message/talk/{roomCid}")
    @SendTo("/receive/rooms/{roomCid}")
    public ChatMessageResponseDto sendMessage(
            @DestinationVariable Long roomCid,
            @Payload CreateChatMessageRequest createChatMessageRequest,
            @AuthenticationPrincipal User user
    ){
        log.info("[TALK_CHAT_ROOM] 채팅 입력 요청이 들어왔습니다.");
        return chatMessageService.sendMessage(roomCid, createChatMessageRequest, user);
    }

    @MessageMapping("/message/leave/{roomCid}")
    @SendTo("/reciece/rooms/{roomCid}")
    public ChatMessageResponseDto leaveMessage(
            @DestinationVariable Long roomCid,
            @AuthenticationPrincipal User user
    ){
        log.info("[LEAVE_CHAT_ROOM] 채팅창 퇴장 요청이 들어왔습니다.");
        return chatMessageService.leaveChatRoom(roomCid, user);
    }

    @GetMapping("/message/rooms/{roomCid}")
    public ResponseEntity<List<ChatMessageResponseDto>> getAllMessage(
            @PathVariable Long roomCid,
            @AuthenticationPrincipal User user
    ){
        log.info("[GET_CHAT_MESSAGE] 채팅메시지 조회 요청이 들어왔습니다.");

        return ResponseEntity.ok(chatMessageService.getAllMessage(roomCid, user));
    }
}