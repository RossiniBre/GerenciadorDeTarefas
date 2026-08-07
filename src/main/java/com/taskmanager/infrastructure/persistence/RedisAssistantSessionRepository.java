package com.taskmanager.infrastructure.persistence;

import com.google.gson.Gson;
import com.taskmanager.domain.assistant.AssistantSession;
import com.taskmanager.domain.repositories.AssistantSessionRepository;
import redis.clients.jedis.JedisPool;

import java.util.Optional;

public class RedisAssistantSessionRepository implements AssistantSessionRepository {

    private static final String KEY_PREFIX = "assistant:session:";
    private static final int TTL_SECONDS = 1800;

    private final JedisPool jedisPool;
    private final Gson gson;

    public RedisAssistantSessionRepository(JedisPool jedisPool, Gson gson) {
        this.jedisPool = jedisPool;
        this.gson = gson;
    }

    @Override
    public Optional<AssistantSession> find(String token) {
        try (var jedis = jedisPool.getResource()) {
            String json = jedis.get(KEY_PREFIX + token);
            if (json == null) {
                return Optional.empty();
            }
            return Optional.of(gson.fromJson(json, AssistantSession.class));
        }
    }

    @Override
    public void save(String token, AssistantSession session) {
        try (var jedis = jedisPool.getResource()) {
            String json = gson.toJson(session);
            jedis.setex(KEY_PREFIX + token, TTL_SECONDS, json);
        }
    }

    @Override
    public void delete(String token) {
        try (var jedis = jedisPool.getResource()) {
            jedis.del(KEY_PREFIX + token);
        }
    }
}