package com.hansdesk.user.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 현재는 BCryptPasswordEncoder를 그대로 가져다 썼다. 사실 BCryptPasswordEncoder 자체가 PasswordEncoder를
 * 구현하고 있기 때문에 BCryptPasswordEncoder를 직접 써도 되지만 추후에 커스텀 PasswordEncoder를 구현한다면
 * BCryptPasswordEncoder를 걷어내고 아래 두 메소드를 구현하면 된다.
 * 이렇게 하면 Component로 선언해서 Autowired로 주입해서 사용할 수 있는 장점이 있다.
 */
@Component
public class UserPasswordEncoder implements PasswordEncoder {

    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String encode(CharSequence rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
