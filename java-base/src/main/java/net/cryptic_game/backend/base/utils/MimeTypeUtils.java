package net.cryptic_game.backend.base.utils;

import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public final class MimeTypeUtils {

    public static final Map<String, CharSequence> MIME_TYPES;

    static {
        MIME_TYPES = parseMimeTypes(MimeTypeUtils.class.getResourceAsStream("/mime.types"));
    }

    private MimeTypeUtils() {
        throw new UnsupportedOperationException();
    }

    private static Map<String, CharSequence> parseMimeTypes(final InputStream mimeTypesFileStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(mimeTypesFileStream, StandardCharsets.UTF_8))) {
            final Map<String, CharSequence> result = new HashMap<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                final String[] tokens = Arrays.stream(line.split("[ \t\n\r\f]")).filter(entry -> !entry.isBlank()).toArray(String[]::new);
                final CharSequence mimeType = AsciiString.cached(tokens[0]);
                for (int i = 1; i < tokens.length; i++) {
                    result.put(tokens[i].toLowerCase(Locale.ENGLISH), mimeType);
                }
            }
            return result;
        } catch (IOException e) {
            log.error("Could not load mime types.", e);
            return Collections.emptyMap();
        }
    }

    public static CharSequence getMimeType(final File file) {
        return getMimeType(file.getName());
    }

    public static CharSequence getMimeType(final Path path) {
        return getMimeType(path.getFileName().toString());
    }

    public static CharSequence getMimeType(final Resource resource) {
        final String filename = resource.getFilename();
        return filename == null ? null : getMimeType(filename);
    }

    public static CharSequence getMimeType(final String filename) {
        final String filenameExtension = StringUtils.getFilenameExtension(filename);
        return filenameExtension == null ? null : MIME_TYPES.get(filenameExtension.toLowerCase(Locale.ENGLISH));
    }
}
