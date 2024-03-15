package org.supercoding.supertime.chat.web.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.supercoding.supertime.chat.web.entity.ChatRoomEntity;
import org.supercoding.supertime.chat.web.entity.ChatRoomMemberEntity;
import org.supercoding.supertime.user.entity.user.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GetUserListResDto {

    @Schema(description = "요청의 성공 상태", example = "true")
    private Boolean success;

    @Schema(description = "요청 코드의 status", example = "200")
    private Integer code;

    @Schema(description = "요청 코드의 에러 메시지", example = "잘못되었습니다")
    private String message;

    @Schema(description = "유저 리스트", example = "[`참여자 A`,`참여자 B`]")
    private List<String> userList;

    public static GetUserListResDto successResult(final String message, final ChatRoomEntity chatRoom) {

        List<String> userList = new ArrayList<>();
        List<ChatRoomMemberEntity> chatRoomMembers = chatRoom.getChatRoomMemberList();

        if (chatRoomMembers != null && !chatRoomMembers.isEmpty()) {
            for (ChatRoomMemberEntity chatRoomMember : chatRoomMembers) {
                UserEntity user = chatRoomMember.getUser();
                if (user != null) {
                    userList.add(user.getUserNickname());
                }
            }
        }

        return GetUserListResDto.builder()
                .success(true)
                .code(200)
                .message(message)
                .userList(userList)
                .build();
    }
}
