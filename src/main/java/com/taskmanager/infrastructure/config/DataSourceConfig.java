package com.taskmanager.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    @Bean
    public Connection connection(DatabaseConfig databaseConfig) throws SQLException {
        return DriverManager.getConnection(
                databaseConfig.getUrl(),
                databaseConfig.getUser(),
                databaseConfig.getPassword()
        );
    }
}