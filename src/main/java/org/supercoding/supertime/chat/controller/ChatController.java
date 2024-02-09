package org.supercoding.supertime.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RestController;
import org.supercoding.supertime.chat.dto.ChatMessage;
import org.supercoding.supertime.chat.dto.request.CreateChatMessageRequest;
import org.supercoding.supertime.chat.dto.response.ChatMessageResponseDto;
import org.supercoding.supertime.chat.repository.ChatRepository;
import org.supercoding.supertime.chat.service.ChatMessageService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRepository chatRepository;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/enterUser")
    public void enterUser(@Payload ChatMessage chat, SimpMessageHeaderAccessor headerAccessor) {
        chatRepository.plusUserCnt(chat.getRoomId());
        String userUUID = chatRepository.addUser(chat.getRoomId(), chat.getSender());

        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());

        chat.setMessage(chat.getSender() + " 님 입장!!");
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload ChatMessage chat) {
        log.info("CHAT {}", chat);
        chat.setMessage(chat.getMessage());
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);

    }

    @MessageMapping("/messages/enter/{roomCid}")
    @SendTo("/receive/rooms/{roomCid}")
    public ChatMessageResponseDto enterChatRoom(
            @DestinationVariable Long roomCid,
            @Payload CreateChatMessageRequest createChatMessageRequest,
            @AuthenticationPrincipal User user
            ){
        log.debug("[ENTER_CHAT_ROOM] 채팅창 입장 요청이 들어왔습니다.");
        return chatMessageService.enterChatRoom(roomCid, createChatMessageRequest, user);
    }

    @MessageMapping("/message/talk/{roomCid}")
    @SendTo("/receive/rooms/{roomCid}")
    public ChatMessageResponseDto sendMessage(
            @DestinationVariable Long roomCid,
            @Payload CreateChatMessageRequest createChatMessageRequest,
            @AuthenticationPrincipal User user
    ){
        log.debug("[TALK_CHAT_ROOM] 채팅 입력 요청이 들어왔습니다.");
        return chatMessageService.sendMessage(roomCid, createChatMessageRequest, user);
    }
}