package com.example.redis.redis;

import com.example.redis.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 기본적으로 application.properties 파일에 아래 설정만 추가해줘도 Redis와의 연동은 이루어지며 기본 동작을 수행하는데 문제가 없다.
 *  spring.redis.host=192.168.1.123
 *  spring.redis.port=6379
 *
 * 이 클래스는 여러가지 설정을 통해서 동작을 커스터마이징하기 위해 필요하다.
 * 아래 예제로 제시된 메소드를 분석하면 쉽게 그 기능을 알 수 있다.
 */
@Configuration
public class RedisConfig {

    // application.properties 설정이 적용된 default RedisConnectionFactory객체가 주입된다.
    // 직접 생성하려면 아래 Bean 메소드처럼 구현하면 된다. 하지만 Spring Boot의 설정파일로 충분하므로 직접 생성할 일은 없다.
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    /**
     * RedisConnectionFactory Bean 구현 예제. 자동 주입되므로 특별한 경우가 아니면 필요하지 않다.
     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
//        return lettuceConnectionFactory;
//    }


    /**
     * Object를 Redis에 set, get 하기위한 Template 작성 예제.
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserVo.class));

        return redisTemplate;
    }
}