package com.themoneywallet.usermanagmentservice.service;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {

    /*
    private final RedisTemplate<String, Long> redisTemplate;

    public void saveData(String key, Long value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Long getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteData(String key) {
        redisTemplate.delete(key);
    }
         */
}
