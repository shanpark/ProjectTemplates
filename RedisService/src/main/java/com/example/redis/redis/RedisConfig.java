package com.example.redis.redis;

import com.example.redis.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 기본적으로 application.properties 파일에 아래 설정만 추가해줘도 Redis와의 연동은 이루어지며 기본 동작을 수행하는데 문제가 없다.
 *  spring.redis.host=192.168.1.123
 *  spring.redis.port=6379
 *
 * 이 클래스는 여러가지 설정을 통해서 동작을 커스터마이징하기 위해 필요하다.
 * 아래 예제로 제시된 메소드를 분석하면 쉽게 그 기능을 알 수 있다.
 * 사실 Configuration 작업은 거의 없다. 적절한 Bean을 반환하는 메소드 구현만 모아놓은 정도이다.
 */
@Configuration
public class RedisConfig {

    // application.properties 설정이 적용된 default RedisConnectionFactory객체가 주입된다.
    // 직접 생성하려면 아래 Bean 메소드처럼 구현하면 된다. 하지만 Spring Boot의 설정파일로 충분하므로 직접 생성할 일은 없다.
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

//    /**
//     * RedisConnectionFactory Bean 구현 예제. 자동 주입되므로 특별한 경우가 아니면 필요하지 않다.
//     */
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory() {
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory();
//        return lettuceConnectionFactory;
//    }


    /**
     * Object를 Redis에 set, get 하기위한 Template 작성 예제.
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(UserVo.class));

        return redisTemplate;
    }

    /**
     * Message를 수신하는 custom subscriber를 정의하여 사용.
     * 여기서 custom subscriber는 RedisMessageSubscriber 클래스이며 MessageListener 인터페이스를 구현한다.
     * onMessage()를 override하여 수신 동작을 구현하면 된다.
     */
    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber());
    }

    /**
     * RedisMessageListenerContainer is a class provided by Spring Data Redis which provides asynchronous behavior
     * for Redis message listeners. This is called internally and, according to the Spring Data Redis documentation
     * – “handles the low level details of listening, converting and message dispatching.”
     * 즉, Spring Data Redis에서 내부적으로 여러가지 처리를 위해서 호출되므로 구현해줘야 한다. 직접 사용할 일은 없다.
     */
    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    /**
     * Message를 publish하는 custom publisher를 생성 및 반환.
     * 사실 publish의 경우에는 redisTemplate.convertAndSend()를 사용하면 된다. 따라서 굳이 이렇게 Bean을 만들어서 사용할 필요는 없다.
     * 하지만 publisher의 사용 패턴을 Spring 스타일로 맞추기위해서 이렇게 구현한 것 뿐이다.
     * 즉, 이 Bean이 꼭 필요한 건 아니고 redisTemplate.convertAndSend()를 직접 사용하여 publish하면 된다.
     */
    @Bean
    MessagePublisher redisPublisher() {
        return new RedisMessagePublisher(redisTemplate(), topic());
    }

    /**
     * topic(Redis Channel)을 생성, 반환하는 Bean 정의.
     * 역시 꼭 필요한 건 아니고 ChannelTopic 객체를 직접 만들어서 사용해도 되지만 Spring 스타일로 구현한 것 뿐이다.
     */
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic("myChannel");
    }
}