package net.cryptic_game.backend.base;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.ConfigValue;
import net.cryptic_game.backend.base.sql.SQLServerType;
import org.apache.logging.log4j.Level;

import java.util.Objects;

@Config
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

    @ConfigValue("response_timeout")
    private int responseTimeout = 20;

    @ConfigValue(value = "log_level", comment = "Available LogLevel's: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE & ALL")
    private String logLevel = Level.WARN.toString();

    @ConfigValue(value = "sentry_dsn", comment = "To disable Sentry leave this empty.")
    private String sentryDsn = "";

    @ConfigValue("use_unix_socket")
    private boolean useUnixSocket = true;

    @ConfigValue("unix_socket_path")
    private String unixSocketPath = "/var/run/cryptic.sock";

    public String getSqlServerLocation() {
        return this.sqlServerLocation;
    }

    public void setSqlServerLocation(final String sqlServerLocation) {
        this.sqlServerLocation = sqlServerLocation;
    }

    public String getSqlServerType() {
        return sqlServerType;
    }

    public void setSqlServerType(final String sqlServerType) {
        this.sqlServerType = sqlServerType;
    }

    public String getSqlServerUsername() {
        return sqlServerUsername;
    }

    public void setSqlServerUsername(final String sqlServerUsername) {
        this.sqlServerUsername = sqlServerUsername;
    }

    public String getSqlServerPassword() {
        return sqlServerPassword;
    }

    public void setSqlServerPassword(final String sqlServerPassword) {
        this.sqlServerPassword = sqlServerPassword;
    }

    public String getSqlServerDatabase() {
        return sqlServerDatabase;
    }

    public void setSqlServerDatabase(final String sqlServerDatabase) {
        this.sqlServerDatabase = sqlServerDatabase;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(final String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public boolean isProductive() {
        return productive;
    }

    public void setProductive(final boolean productive) {
        this.productive = productive;
    }

    public int getSessionExpire() {
        return sessionExpire;
    }

    public void setSessionExpire(final int sessionExpire) {
        this.sessionExpire = sessionExpire;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(final int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(final String logLevel) {
        this.logLevel = logLevel;
    }

    public String getSentryDsn() {
        return sentryDsn;
    }

    public void setSentryDsn(final String sentryDsn) {
        this.sentryDsn = sentryDsn;
    }

    public boolean isUseUnixSocket() {
        return useUnixSocket;
    }

    public void setUseUnixSocket(final boolean useUnixSocket) {
        this.useUnixSocket = useUnixSocket;
    }

    public String getUnixSocketPath() {
        return unixSocketPath;
    }

    public void setUnixSocketPath(final String unixSocketPath) {
        this.unixSocketPath = unixSocketPath;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseConfig)) return false;
        BaseConfig that = (BaseConfig) o;
        return isProductive() == that.isProductive()
                && getSessionExpire() == that.getSessionExpire()
                && getResponseTimeout() == that.getResponseTimeout()
                && isUseUnixSocket() == that.isUseUnixSocket()
                && getSqlServerLocation().equals(that.getSqlServerLocation())
                && getSqlServerType().equals(that.getSqlServerType())
                && getSqlServerUsername().equals(that.getSqlServerUsername())
                && getSqlServerPassword().equals(that.getSqlServerPassword())
                && getSqlServerDatabase().equals(that.getSqlServerDatabase())
                && getStorageLocation().equals(that.getStorageLocation())
                && getLogLevel().equals(that.getLogLevel())
                && getSentryDsn().equals(that.getSentryDsn())
                && getUnixSocketPath().equals(that.getUnixSocketPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSqlServerLocation(), getSqlServerType(), getSqlServerUsername(), getSqlServerPassword(),
                getSqlServerDatabase(), getStorageLocation(), isProductive(), getSessionExpire(), getResponseTimeout(),
                getLogLevel(), getSentryDsn(), isUseUnixSocket(), getUnixSocketPath());
    }

    @Override
    public String toString() {
        return "BaseConfig{"
                + "sqlServerLocation='" + sqlServerLocation + '\''
                + ", sqlServerType='" + sqlServerType + '\''
                + ", sqlServerUsername='" + sqlServerUsername + '\''
                + ", sqlServerPassword='" + sqlServerPassword + '\''
                + ", sqlServerDatabase='" + sqlServerDatabase + '\''
                + ", storageLocation='" + storageLocation + '\''
                + ", productive=" + productive
                + ", sessionExpire=" + sessionExpire
                + ", responseTimeout=" + responseTimeout
                + ", logLevel='" + logLevel + '\''
                + ", sentryDsn='" + sentryDsn + '\''
                + ", useUnixSocket=" + useUnixSocket
                + ", unixSocketPath='" + unixSocketPath + '\''
                + '}';
    }
}
