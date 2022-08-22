package com.atg.gulimall.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

@SpringBootTest
class GulimallProductApplicationTests {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
    }

    @Test
    void test(){
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello", UUID.randomUUID().toString());

        System.out.println(ops.get("hello"));
    }

}
