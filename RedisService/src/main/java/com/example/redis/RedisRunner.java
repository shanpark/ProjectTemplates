package com.example.redis;

import com.example.redis.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
public class RedisRunner implements ApplicationRunner {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void run(ApplicationArguments args) {
        // StringRedisTemplate는 아무 설정 없이도 아래 기본 동작을 수행하는 데 문제가 없다.
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set("name", "saelobi");
        values.set("framework", "spring");
        values.set("message", "hello world");
        String message = values.get("message");
        System.out.println(message);

        UserVo userVo = new UserVo();
        userVo.setName("홍길동");
        userVo.setEmail("honggildong@gmail.com");

        ValueOperations<String, Object> valuesForObj = redisTemplate.opsForValue();
        valuesForObj.set("user", userVo);
        userVo = (UserVo) valuesForObj.get("user");
        System.out.println("name: " + userVo.getName() + ", email: " + userVo.getEmail());
    }
}
