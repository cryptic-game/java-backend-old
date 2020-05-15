package net.cryptic_game.backend.base;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

class LoggerOutputStream extends OutputStream {

    private static final String LINE_SEPARATOR = "\n";
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Logger logger;
    private final Level level;
    private final PrintStream original;
    private String memory;

    LoggerOutputStream(final String name, final Level level, final PrintStream original) {
        this.logger = LogManager.getLogger(name);
        this.level = level;
        this.original = original;
        this.memory = "";
    }

    @Override
    public void write(final int b) {
        final byte[] bytes = new byte[1];
        bytes[0] = (byte) (b & 0xff);
        final String current = new String(bytes, CHARSET);

        if (!current.equals(LINE_SEPARATOR)) this.memory += current;
        else flush();
    }

    @Override
    public void flush() {
        if (this.memory.startsWith("\tat")) {
            this.original.println(this.memory);
        } else if (this.memory.startsWith("Exception in thread \"")) {
            final String[] msg = this.memory.split(":", 2);
            if (msg.length == 2) this.logger.log(this.level, msg[1].strip());
            else this.logger.log(this.level, this.memory);
            this.original.println(this.memory);
        } else {
            this.logger.log(this.level, this.memory);
        }
        this.memory = "";
    }
}
