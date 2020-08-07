package com.hansdesk.rest.service;

import com.hansdesk.rest.vo.UserVO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional
public class UserService {

//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private UserPasswordEncoder passwordEncoder;

    public Authentication authenticate(String email, String password) {
        // TODO 현재는 무조건 통과시키기 위한 코드이다.
        //  참고로 DB가 없는 상태에서 샘플을 실행하기 위해서 Mybatis관련된 부분을 전체적으로 주석처리하였다.
        UserVO userVO = new UserVO();
        userVO.setUid(1L);
        userVO.setEmail(email);
        userVO.setNickname("Nickname");
        userVO.setPassword(null);

        return new UsernamePasswordAuthenticationToken(userVO, null, null);

        // 여기서 인증 중에 에러가 발생하면 exeption을 발생시키고 Authentication 객체를 반환하지 않는다.
        // exception이 발생하면 Body없이 403 에러코드가 반환된다.
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
