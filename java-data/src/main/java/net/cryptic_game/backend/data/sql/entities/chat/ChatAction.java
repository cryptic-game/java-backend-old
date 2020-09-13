package net.cryptic_game.backend.data.sql.entities.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This enum defines the type of a chat notification.
 *
 * @since 0.3.0
 */
@Getter
@RequiredArgsConstructor
public enum ChatAction {

    MEMBER_JOIN,
    MEMBER_LEAVE,

    SEND_MESSAGE,
    MESSAGE_DELETED,
    WHISPER_MESSAGE,

    CHANNEL_ADDED,
    CHANNEL_RENAMED,
    CHANNEL_DELETE
}
