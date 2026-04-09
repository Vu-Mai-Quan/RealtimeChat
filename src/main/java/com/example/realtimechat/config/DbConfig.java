package com.example.realtimechat.config;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.AbstractEntityManagerFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.realtimechat.db1")
@EnableConfigurationProperties(JpaProperties.class)
public class DbConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.db1")
    public DataSource dataSourceOne() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean
    AbstractEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
                                                          @NonNull EntityManagerFactoryBuilder builder,
                                                          @NonNull JpaProperties properties) {


        return builder.dataSource(dataSource)
                .packages("com.example.realtimechat.db1")
                .properties(properties.getProperties())
                .build();
    }

    @Bean
    AbstractPlatformTransactionManager transactionManager(@NonNull AbstractEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(
                Objects.requireNonNull(entityManagerFactory.getObject()));

    }
}
