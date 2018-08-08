package com.hansdesk.rest.security;

import com.hansdesk.rest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JWT 적용을 위한 최소한의 설정만 하고 있다.
 * 각 라인의 주석을 참고하여 필요한 동작들을 깨지 않는 범위에서 필요한 설정을 추가하면 된다.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF는 POST요청을 하기 전에 GET으로 어떤 페이지를 요청하고 그 때 CSRF 토큰을 받아와서 그 토큰을 이어지는 POST 요청에 같이 전송함으로써
        // 보안을 통과하는 것이다. JWT를 이용하는 RESTful API는 일반적으로 이렇게 GET -> POST로 이어지는 요청형태가 아니므로 CSRF를 적용하기에
        // 적합하지 않다. 또한 JWT가 CSRF 토큰의 역할을 수행하는데 부족함이 없다고 하므로 CSRF를 disable 시켜도 좋다.
        http.csrf().disable();

        // '/login' 요청만 모두 허용이고 나머지는 인증 사용자만 access 가능하도록 설정.
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .anyRequest().authenticated();

        // '/login' 요청에 대한 필터 설정. 아래 JWTAuthenticationFilter보다 먼저 추가해야 한다.
        // 순서가 바뀌면 /login 경로에 대해서 JWTAuthenticationFilter보다 필터가 먼저 적용되므로 오동작의 우려가 있다.
        http.addFilterBefore(new JWTLoginFilter("/login", userService), UsernamePasswordAuthenticationFilter.class);

        // 나머지 요청에 대한 필터 설정.
        http.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
