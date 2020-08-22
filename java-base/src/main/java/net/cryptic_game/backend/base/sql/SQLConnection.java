package net.cryptic_game.backend.base.sql;

import net.cryptic_game.backend.base.sql.models.TableModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Properties;

public final class SQLConnection {

    private final Configuration configuration;
    private SessionFactory factory;

    public SQLConnection() {
        this.configuration = new Configuration();
    }

    public void init(final SQLServer sqlServer, final boolean debug) {
        this.configuration.addProperties(this.generateSettings(sqlServer, debug));

        final ServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySettings(this.configuration.getProperties())
                .build();

        this.factory = this.configuration.buildSessionFactory(registry);
    }

    private Properties generateSettings(final SQLServer sqlServer, final boolean debug) {
        final Properties settings = new Properties();
        settings.put(Environment.DRIVER, sqlServer.getSqlServerType().getDriver());
        settings.put(Environment.URL, "jdbc:" + sqlServer.getSqlServerType().getUrlPrefix() + ":" + sqlServer.getLocation() + "/"
                + sqlServer.getDatabase() + (sqlServer.getLocation().startsWith("//")
                ? "?autoReconnect=true&useUnicode=true&characterEncoding=utf-8"
                : !sqlServer.getLocation().contains("tcp") ? ";AUTO_SERVER=TRUE" : ""));
        settings.put(Environment.AUTOCOMMIT, "false");
        settings.put(Environment.USER, sqlServer.getUsername());
        settings.put(Environment.PASS, sqlServer.getPassword());
        settings.put(Environment.DIALECT, sqlServer.getSqlServerType().getDialect());
        settings.put(Environment.HBM2DDL_AUTO, "update");
        settings.put(Environment.HBM2DDL_CHARSET_NAME, StandardCharsets.UTF_8.toString());
        settings.put(Environment.CONNECTION_PREFIX + ".CharSet", StandardCharsets.UTF_8.toString());
        settings.put(Environment.CONNECTION_PREFIX + ".characterEncoding", StandardCharsets.UTF_8.toString());
        settings.put(Environment.CONNECTION_PREFIX + ".useUnicode", "true");
        settings.put("connection.autoReconnect", "true");
        settings.put("connection.autoReconnectForPools", "true");
        settings.put("connection.is-connection-validation-required", "true");
        settings.put(Environment.JDBC_TIME_ZONE, "UTC");
        settings.put("hibernate.hikari.connectionTimeout", "10000"); // 10 seconds
        settings.put("hibernate.hikari.initializationFailTimeout", "30000"); // 30 seconds

        if (debug) {
            settings.put(Environment.SHOW_SQL, "true");
            settings.put(Environment.FORMAT_SQL, "true");
        }

        return settings;
    }

    public void addEntity(final Class<? extends TableModel> entity) throws SQLException {
        if (this.factory == null) this.configuration.addAnnotatedClass(entity);
        else throw new SQLException("It's too late to register any more entities.");
    }

    public Session openSession() throws HibernateException {
        return this.factory.openSession();
    }

    public void close() {
        this.factory.close();
    }
}

