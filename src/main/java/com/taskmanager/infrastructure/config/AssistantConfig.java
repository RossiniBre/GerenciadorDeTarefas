package com.taskmanager.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "assistant")
public class AssistantConfig {

    public static final String MODEL = "openrouter/free";
    public static final int DAILY_LIMIT = 50;

    private String apiKey;

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}