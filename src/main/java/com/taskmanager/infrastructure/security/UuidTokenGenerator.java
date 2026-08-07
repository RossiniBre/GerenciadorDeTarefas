package com.taskmanager.infrastructure.security;

import com.taskmanager.domain.security.TokenGenerator;
import java.util.UUID;

public class UuidTokenGenerator implements TokenGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}