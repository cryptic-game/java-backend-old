package net.cryptic_game.backend.data.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatAction {

    MEMBER_JOIN("member-join"),
    MEMBER_LEAVE("member-leave"),

    SEND_MESSAGE("send-message"),
    WHISPER_MESSAGE("whisper-message"),

    CHANNEL_DELETE("channel-delete");

    private final String value;
}
