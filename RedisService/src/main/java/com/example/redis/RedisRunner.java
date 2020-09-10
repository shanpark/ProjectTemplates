package com.example.redis;

import com.example.redis.redis.MessagePublisher;
import com.example.redis.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisRunner implements ApplicationRunner {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    MessagePublisher messagePublisher;

    @Override
    public void run(ApplicationArguments args) {
        // StringRedisTemplate는 아무 설정 없이도 아래 기본 동작을 수행하는 데 문제가 없다.
        ValueOperations<String, String> values = stringRedisTemplate.opsForValue();
        values.set("name", "saelobi");
        values.set("framework", "spring");
        values.set("message", "hello world");
        String message = values.get("message");
        System.out.println(message);

        // UserVo 객체를 키값으로 set/get 하는 예제이다.
        UserVo userVo = new UserVo();
        userVo.setName("홍길동");
        userVo.setEmail("honggildong@gmail.com");

        ValueOperations<String, Object> valuesForObj = redisTemplate.opsForValue();
        valuesForObj.set("user", userVo);
        userVo = (UserVo) valuesForObj.get("user");
        if (userVo != null)
            System.out.println("name: " + userVo.getName() + ", email: " + userVo.getEmail());

        // Redis Pub/Sub 예제이다.
        message = "Message " + UUID.randomUUID();
        messagePublisher.publish(message); // RedisMessageSubscriber의 onMessage()가 호출될 것이다.
    }
}
