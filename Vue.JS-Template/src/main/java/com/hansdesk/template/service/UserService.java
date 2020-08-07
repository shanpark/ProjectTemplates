package com.hansdesk.template.service;

import com.hansdesk.template.vo.UserVo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {
    public Authentication authenticate(String email, String password) {
        // 여기서 인증 중에 에러가 발생하면 exeption을 발생시키고 Authentication 객체를 반환하지 않는다.
        // exception이 발생하면 Body없이 403 에러코드가 반환된다.

        if (email.equals("shanpark@naver.com") && password.equals("lalala")) {
            // 여기서는 단순 샘플이므로 모든 요청을 검증없이 통과시킨다. nickname = '테스터'
            UserVo userVo = new UserVo();
            userVo.setUid(5L);
            userVo.setEmail(email);
            userVo.setNickname("테스터");
            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
            return new UsernamePasswordAuthenticationToken(userVo, null, authorities);
        } else {
            throw new BadCredentialsException("login.no-user-found");
        }

//        UserVO userVO = userMapper.getUserByEmail(email);
//        if (userVO != null) {
//            if (!passwordEncoder.matches(password, userVO.getPassword()))
//                throw new BadCredentialsException("login.invalid-password"); // exception message에 messages.properties의 키 값을 넣으면 login.html 템플릿에서 알아서 localized 메시지를 보여준다.
//
//            List<String> userRoles = userMapper.getUserRoleByUid(userVO.getUid());
//            if (userRoles == null)
//                throw new BadCredentialsException("login.no-authority");
//            userVO.setPassword(null);
//
//            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            for (String role : userRoles)
//                authorities.add(new SimpleGrantedAuthority(role));
//            return new UsernamePasswordAuthenticationToken(userVO, null, authorities);
//        }
//
//        throw new BadCredentialsException("login.no-user-found");
    }
}
