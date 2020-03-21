package net.cryptic_game.backend.base.api.notification;

public class ApiNotificationException extends IllegalStateException {

    public ApiNotificationException(final String exception) {
        super(exception);
    }
}
