package com.kamal.urlshortener;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest
public class TestService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void checkRedisConnection() {
        stringRedisTemplate.opsForValue().set("Fruit", "Apple");
        System.out.println(stringRedisTemplate.opsForValue().get("Fruit"));
    }

}
