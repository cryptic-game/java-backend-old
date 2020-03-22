package net.cryptic_game.backend.base.sql;

import org.hibernate.dialect.*;

import java.sql.Driver;

/**
 * All available Drivers:
 * <ul>
 *     <li>
 *         PostgreSQL
 *         <ul>
 *             <li>pg_08_1: 8.1</li>
 *             <li>pg_08_2: 8.2</li>
 *             <li>pg_09_0: 8.2</li>
 *             <li>pg_09_1: 8.2</li>
 *             <li>pg_09_2: 8.2</li>
 *             <li>pg_09_3: 8.2</li>
 *             <li>pg_09_4: 8.2</li>
 *             <li>pg_09_5: 8.2</li>
 *             <li>pg_10_0: 8.2</li>
 *         </ul>
 *     </li>
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

