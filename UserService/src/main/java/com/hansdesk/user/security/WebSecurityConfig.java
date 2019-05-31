package com.hansdesk.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserAuthenticationProvider authenticationProvider;

    @Bean // success handler는 @Autowired로 멤버를 선언해서 사용하면 안되고 반드시 @Bean으로 등록해서 지정해야 한다.
    AuthenticationSuccessHandler successHandler(String defaultSuccessUrl) {
        return new UserAuthenticationSuccessHandler(defaultSuccessUrl);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/error", "/css/**", "/js/**", "/img/**", "/email-dup-check", "/nickname-dup-check", "/sign-up", "/sign-up-next").permitAll()
                .antMatchers("/auth/admin/*").hasRole("ADMIN") // example
                .antMatchers("/auth/*").hasAnyRole("ADMIN", "USER") // example
                .anyRequest().authenticated();

        http.formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/authenticate")
                .failureUrl("/login?error") //default
//                .defaultSuccessUrl("/home") // 아래 successHandler를 지정하면 이건 지정할 필요가 없다.
                .successHandler(successHandler("/home")) // 쿠키를 저장할 타이밍을 잡기 위해서 success handler를 넣는다.
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll();

        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }
}
