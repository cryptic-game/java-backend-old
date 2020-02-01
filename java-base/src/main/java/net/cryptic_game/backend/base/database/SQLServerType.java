package net.cryptic_game.backend.base.database;

import org.hibernate.dialect.*;

import java.sql.Driver;

/**
 * All available Drivers:
 * <ul>
 *     <li>
 *         MariaDB
 *         <ul>
 *             <li>mariadb_05_3: 05.3</li>
 *             <li>mariadb_10_0: 10.0</li>
 *             <li>mariadb_10_2: 10.2</li>
 *             <li>mariadb_10_3: 10.3</li>
 *         </ul>
 *     </li>
 *     <li>
 *          MySQL
 *          <ul>
 *              <li>mysql_5_0: 5.0</li>
 *              <li>mysql_5_5: 5.5</li>
 *              <li>mysql_5_7: 5.7</li>
 *              <li>mysql_8_0: 8.0</li>
 *          </ul>
 *      </li>
 * </ul>
 */
public enum SQLServerType {

    MARIADB_53_0("mariadb_05_3", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB53Dialect.class),
    MARIADB_10_0("mariadb_10_0", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB10Dialect.class),
    MARIADB_10_2("mariadb_10_2", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB102Dialect.class),
    MARIADB_10_3("mariadb_10_3", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB103Dialect.class),

    MYSQL_5_0("mysql_5_0", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL5Dialect.class),
    MYSQL_5_5("mysql_5_5", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL55Dialect.class),
    MYSQL_5_7("mysql_5_7", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL57Dialect.class),
    MYSQL_8_0("mysql_8_0", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL8Dialect.class),

    NONE("none", "none", Driver.class, Dialect.class);

    final String name;
    final String urlPrefix;
    final String driver;
    final String dialect;

    SQLServerType(final String name, final String urlPrefix, final Class<? extends Driver> driver, final Class<? extends Dialect> dialect) {
        this.name = name;
        this.urlPrefix = urlPrefix;
        this.driver = driver.getName();
        this.dialect = dialect.getName();
    }

    public static SQLServerType getServer(final String name) {
        for (final SQLServerType value : SQLServerType.values())
            if (value.getName().equals(name.strip().toLowerCase())) return value;
        return SQLServerType.NONE;
    }

    public String getName() {
        return this.name;
    }

    public String getUrlPrefix() {
        return this.urlPrefix;
    }

    public String getDriver() {
        return this.driver;
    }

    public String getDialect() {
        return this.dialect;
    }
}

