package net.cryptic_game.backend.base.sql;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.MariaDB102Dialect;
import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.dialect.MariaDB10Dialect;
import org.hibernate.dialect.MariaDB53Dialect;
import org.hibernate.dialect.MySQL55Dialect;
import org.hibernate.dialect.MySQL57Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.dialect.PostgreSQL91Dialect;
import org.hibernate.dialect.PostgreSQL92Dialect;
import org.hibernate.dialect.PostgreSQL93Dialect;
import org.hibernate.dialect.PostgreSQL94Dialect;
import org.hibernate.dialect.PostgreSQL95Dialect;
import org.hibernate.dialect.PostgreSQL9Dialect;

import java.sql.Driver;

/**
 * All available Drivers:
 * <ul>
 *     <li>
 *         {@code h2}: H2 (Embedded Database, only for developing / testing)
 *     </li>
 *     <li>
 *         PostgreSQL
 *         <ul>
 *             <li>{@code pg_08_1}: 8.1</li>
 *             <li>{@code pg_08_2}: 8.2</li>
 *             <li>{@code pg_09_0}: 8.2</li>
 *             <li>{@code pg_09_1}: 8.2</li>
 *             <li>{@code pg_09_2}: 8.2</li>
 *             <li>{@code pg_09_3}: 8.2</li>
 *             <li>{@code pg_09_4}: 8.2</li>
 *             <li>{@code pg_09_5}: 8.2</li>
 *             <li>{@code pg_10_0}: 8.2</li>
 *         </ul>
 *     </li>
 *     <li>
 *         MariaDB
 *         <ul>
 *             <li>{@code myriadb_05_3}: 05.3</li>
 *             <li>{@code myriadb_10_0}: 10.0</li>
 *             <li>{@code myriadb_10_2}: 10.2</li>
 *             <li>{@code myriadb_10_3}: 10.3</li>
 *         </ul>
 *     </li>
 *     <li>
 *          MySQL
 *          <ul>
 *              <li>{@code mysql_5_0}: 5.0</li>
 *              <li>{@code mysql_5_5}: 5.5</li>
 *              <li>{@code mysql_5_7}: 5.7</li>
 *              <li>{@code mysql_8_0}: 8.0</li>
 *          </ul>
 *      </li>
 * </ul>
 */
public enum SQLServerType {

    POSTGRE_SQL_08_1("pg_08_1", "postgresql", org.postgresql.Driver.class, PostgreSQL81Dialect.class),
    POSTGRE_SQL_08_2("pg_08_2", "postgresql", org.postgresql.Driver.class, PostgreSQL82Dialect.class),
    POSTGRE_SQL_09_0("pg_09_0", "postgresql", org.postgresql.Driver.class, PostgreSQL9Dialect.class),
    POSTGRE_SQL_09_1("pg_09_1", "postgresql", org.postgresql.Driver.class, PostgreSQL91Dialect.class),
    POSTGRE_SQL_09_2("pg_09_2", "postgresql", org.postgresql.Driver.class, PostgreSQL92Dialect.class),
    POSTGRE_SQL_09_3("pg_09_3", "postgresql", org.postgresql.Driver.class, PostgreSQL93Dialect.class),
    POSTGRE_SQL_09_4("pg_09_4", "postgresql", org.postgresql.Driver.class, PostgreSQL94Dialect.class),
    POSTGRE_SQL_09_5("pg_09_5", "postgresql", org.postgresql.Driver.class, PostgreSQL95Dialect.class),
    POSTGRE_SQL_10_0("pg_10_0", "postgresql", org.postgresql.Driver.class, PostgreSQL10Dialect.class),

    MARIADB_05_3("mariadb_05_3", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB53Dialect.class),
    MARIADB_10_0("mariadb_10_0", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB10Dialect.class),
    MARIADB_10_2("mariadb_10_2", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB102Dialect.class),
    MARIADB_10_3("mariadb_10_3", "mariadb", org.mariadb.jdbc.Driver.class, MariaDB103Dialect.class),

    MYSQL_5_0("mysql_5_0", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL5Dialect.class),
    MYSQL_5_5("mysql_5_5", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL55Dialect.class),
    MYSQL_5_7("mysql_5_7", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL57Dialect.class),
    MYSQL_8_0("mysql_8_0", "mysql", com.mysql.cj.jdbc.Driver.class, MySQL8Dialect.class),

    H2("h2", "h2", org.h2.Driver.class, H2Dialect.class),

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

