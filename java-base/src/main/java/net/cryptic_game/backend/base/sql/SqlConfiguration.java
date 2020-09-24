package net.cryptic_game.backend.base.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.Bootstrap;
import org.hibernate.cfg.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "net.cryptic_game.backend.data.sql.repositories")
@EnableTransactionManagement
public class SqlConfiguration {

    private final Bootstrap bootstrap;
    private final SqlConfig config;

    public SqlConfiguration(final Bootstrap bootstrap, final SqlConfig config) {
        this.bootstrap = bootstrap;
        this.config = config;
    }

    /**
     * Creates a {@link DataSource}.
     *
     * @return The {@link DataSource}
     */
    @Bean
    DataSource dataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:" + this.config.getLocation() + "/" + this.config.getDatabase());
        config.setUsername(this.config.getUsername());
        config.setPassword(this.config.getPassword());

        config.setAutoCommit(false);

        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(10000); // 10 seconds
        config.setInitializationFailTimeout(30000); // 30 seconds
        config.setValidationTimeout(5000); // 5 seconds

        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);

        try {
            return new HikariDataSource(config);
        } catch (HikariPool.PoolInitializationException e) {
            log.error("Unable to connect to database: {}", e.getMessage());
            this.bootstrap.shutdown();
            return null;
        }
    }

    /**
     * Creates a {@link LocalContainerEntityManagerFactoryBean}.
     *
     * @return The {@link LocalContainerEntityManagerFactoryBean}
     */
    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(this.config.isShowStatements());
        jpaVendorAdapter.setGenerateDdl(true);

        final Properties properties = new Properties();
        properties.put(Environment.CACHE_REGION_FACTORY, "");
        properties.put(Environment.USE_SECOND_LEVEL_CACHE, "true");
        properties.put(Environment.USE_QUERY_CACHE, "true");

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("net.cryptic_game.backend.data.sql.entities");
        entityManagerFactoryBean.setDataSource(this.dataSource());
        //entityManagerFactoryBean.setJpaProperties(properties);

        return entityManagerFactoryBean;
    }

    /**
     * Creates a {@link PlatformTransactionManager}.
     *
     * @param entityManagerFactory The {@link EntityManagerFactory} create {@link PlatformTransactionManager}
     * @return The {@link PlatformTransactionManager}
     */
    @Bean
    PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
