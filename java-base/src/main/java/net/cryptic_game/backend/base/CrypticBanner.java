package net.cryptic_game.backend.base;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class CrypticBanner implements Banner {

    private static final String[] BANNER = {
            "", "  .   ____          _            __ _ _",
            " /\\\\ / ___'_ __ _ _(_)_ __  __ _ \\ \\ \\ \\",
            "( ( )\\___ | '_ | '_| | '_ \\/ _` | \\ \\ \\ \\",
            " \\\\/  ___)| |_)| | | | | || (_| |  ) ) ) )",
            "  '  |____| .__|_| |_|_| |_\\__, | / / / /",
            " =========|_|==============|___/=/_/_/_/"
    };

    @Override
    public void printBanner(final Environment environment, final Class<?> sourceClass, final PrintStream printStream) {
        for (final String line : BANNER) {
            printStream.println(line);
        }

        final Package p = sourceClass.getPackage();
        final String version = p.getImplementationVersion();
        final String title = p.getImplementationTitle();

        printStream.print(AnsiOutput.toString(
                " -> ",
                AnsiColor.GREEN,
                title == null ? "Local Development" : title,
                AnsiStyle.NORMAL,
                version == null ? "" : " v" + version
        ));

        final String springBootVersion = SpringBootVersion.getVersion();
        if (springBootVersion != null) {
            printStream.println(AnsiOutput.toString(
                    ", ",
                    AnsiColor.GREEN,
                    "Spring Boot",
                    AnsiStyle.NORMAL,
                    " v" + springBootVersion
            ));
        }

        printStream.println();
    }
}
