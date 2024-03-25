package org.supercoding.supertime.chat.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.supercoding.supertime.chat.web.dto.ChatRoomDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetUserChatRoomResDto {

    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "채팅방 목록")
    private List<ChatRoomDto> chatRoomList;

    public static GetUserChatRoomResDto successResult(String message, List<ChatRoomDto> chatRoomList) {
        return GetUserChatRoomResDto.builder()
                .success(true)
                .code(200)
                .message(message)
                .chatRoomList(chatRoomList)
                .build();
    }
}
