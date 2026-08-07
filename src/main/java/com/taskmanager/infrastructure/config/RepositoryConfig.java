package com.taskmanager.infrastructure.config;

import com.taskmanager.domain.repositories.TaskRepository;
import com.taskmanager.domain.repositories.UserRepository;
import com.taskmanager.infrastructure.persistence.mysql.MySqlTaskRepository;
import com.taskmanager.infrastructure.persistence.mysql.MySqlUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;

@Configuration
public class RepositoryConfig {

    @Bean
    public TaskRepository taskRepository(Connection connection) {
        return new MySqlTaskRepository(connection);
    }

    @Bean
    public UserRepository userRepository(Connection connection) {
        return new MySqlUserRepository(connection);
    }
}