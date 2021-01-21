package net.cryptic_game.backend.admin.endpoints.website;

import java.util.regex.Pattern;

public final class WebsiteUtils {

    private WebsiteUtils() {
        throw new UnsupportedOperationException();
    }

    private static final Pattern XXS_FILTER = Pattern.compile("<[^ ]+>");


    /**
     * returns true if an xxs attempt might be there.
     *
     * @param content content to prove
     * @return true if html tags might be in the content
     */
    public static boolean checkXxs(final String content) {
        return XXS_FILTER.matcher(content).find();
    }
}
