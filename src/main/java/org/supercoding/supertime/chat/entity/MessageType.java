package org.supercoding.supertime.chat.entity;

public enum MessageType {
    ENTER("입장"),
    TALK("대화"),
    LEAVE("퇴장");

    private final String type;

    MessageType(String type) {this.type = type;}

    public String getType() {return type;}

    private static final String ENTER_MESSAGE_SUFFIX = "님이 채팅방에 입장하셨습니다.";
    private static final String LEAVE_MESSAGE_SUFFIX = "님이 채팅방을 떠났습니다.";

    public static String makeEnterMessage(final String nickname) {
        return nickname + ENTER_MESSAGE_SUFFIX;
    }

    public static String makeLeaveMessage(final String nickname) {
        return nickname + LEAVE_MESSAGE_SUFFIX;
    }
}
