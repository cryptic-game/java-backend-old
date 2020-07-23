package net.cryptic_game.backend.base;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.ConfigValue;
import net.cryptic_game.backend.base.sql.SQLServerType;
import org.apache.logging.log4j.Level;

@Config
@Data
@Setter(AccessLevel.NONE)
public final class BaseConfig {

    @ConfigValue(value = "sql_server_location", comment = "TCP MySQL, MariaDB,...: \"//<hostname>:<port>\"\n"
            + "If you are testing right now use \"../tmp\"\nand for IntelliJ: \"jdbc:h2:<absoluteProjectPath>\\tmp\\cryptic;AUTO_SERVER=TRUE\"")
    private String sqlServerLocation = "//127.0.0.1:3306";

    @ConfigValue(value = "sql_server_type", comment = "List of all available types: "
            + "https://cryptic-game.github.io/java-backend-dev/javadoc/latest/net/cryptic_game/backend/base/sql/SQLServerType.html")
    private String sqlServerType = SQLServerType.MARIADB_10_3.toString();

    @ConfigValue("sql_server_username")
    private String sqlServerUsername = "cryptic";

    @ConfigValue("sql_server_password")
    private String sqlServerPassword = "cryptic";

    @ConfigValue("sql_server_database")
    private String sqlServerDatabase = "cryptic";

    @ConfigValue("storage_location")
    private String storageLocation = "./data";

    @ConfigValue("productive")
    private boolean productive = true;

    @ConfigValue("session_expire")
    private int sessionExpire = 14;

    @ConfigValue(value = "log_level", comment = "Available LogLevel's: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE & ALL")
    private String logLevel = Level.WARN.toString();

    @ConfigValue(value = "sentry_dsn", comment = "To disable Sentry leave this empty.")
    private String sentryDsn = "";
}
