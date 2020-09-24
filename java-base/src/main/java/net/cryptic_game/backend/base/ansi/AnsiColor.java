package net.cryptic_game.backend.base.ansi;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AnsiColor implements AnsiElement {

    BLACK("30"),
    RED("31"),
    GREEN("32"),
    YELLOW("33"),
    BLUE("34"),
    MAGENTA("35"),
    CYAN("36"),
    WHITE("37"),
    BRIGHT_BLACK("90"),
    BRIGHT_RED("91"),
    BRIGHT_GREEN("92"),
    BRIGHT_YELLOW("93"),
    BRIGHT_BLUE("94"),
    BRIGHT_MAGENTA("95"),
    BRIGHT_CYAN("96"),
    BRIGHT_WHITE("97");

    private final String code;

    @Override
    public String toString() {
        return this.code;
    }
}
