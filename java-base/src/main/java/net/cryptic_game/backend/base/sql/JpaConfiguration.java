package net.cryptic_game.backend.base.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.Bootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Slf4j
@Configuration
public class JpaConfiguration {

    private final Bootstrap bootstrap;
    private final SqlConfig config;

    public JpaConfiguration(final Bootstrap bootstrap, final SqlConfig config) {
        this.bootstrap = bootstrap;
        this.config = config;
    }

    @Bean
    DataSource dataSource() {
        final HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:" + this.config.getLocation() + "/" + this.config.getDatabase());
        config.setUsername(this.config.getUsername());
        config.setPassword(this.config.getPassword());

        config.setAutoCommit(false);

        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(10000); // 10 secondsH
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

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource) {
        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setShowSql(this.config.isShowStatements());
        jpaVendorAdapter.setGenerateDdl(true);

        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setPackagesToScan("net.cryptic_game");
        entityManagerFactoryBean.setPackagesToScan("net.getnova");
        entityManagerFactoryBean.setDataSource(this.dataSource());

        return entityManagerFactoryBean;
    }

    @Bean
    PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
