package com.hansdesk.user.service;

import com.hansdesk.user.constant.Role;
import com.hansdesk.user.constant.UserStatus;
import com.hansdesk.user.dto.SignUpForm;
import com.hansdesk.user.dto.SignUpNextForm;
import com.hansdesk.user.mapper.UserMapper;
import com.hansdesk.user.security.UserPasswordEncoder;
import com.hansdesk.user.vo.SignUpUserVO;
import com.hansdesk.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserPasswordEncoder passwordEncoder;

    public boolean registerSignUpUser(SignUpForm signUpForm) {
        try {
            String hashed = passwordEncoder.encode(signUpForm.getPassword());
            signUpForm.setPassword(hashed);

            userMapper.registerSignUpUser(signUpForm);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public SignUpUserVO getSignUpUser(String email) {
        return userMapper.getSignUpUser(email);
    }

    public boolean registerUser(SignUpUserVO signUpUserVO, SignUpNextForm signUpNextForm, Role role) {
        try {
            UserVO userVO = new UserVO();
            userVO.setEmail(signUpUserVO.getEmail());
            userVO.setPassword(signUpUserVO.getPassword());
            userVO.setNickname(signUpUserVO.getNickname());
            userVO.setFirstName(signUpUserVO.getFirstName());
            userVO.setLastName(signUpUserVO.getLastName());
            userVO.setHp(signUpNextForm.getHp());
            userVO.setBirth(signUpNextForm.getBirth());
            userVO.setStatus(UserStatus.ACTIVE);

            userMapper.registerUser(userVO);
            userMapper.addUserRole(userVO.getUid(), role);

            userMapper.deleteSignUpUser(userVO.getEmail());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication authenticate(String email, String password) {
        // 이 코드는 테스트를 위해서 무조건 인증을 성공하도록 하는 코드이며 실제 코드는 아래 주석처리된 코드와 비슷한 로직을 갖게 될 것이다.
        UserVO userVO = new UserVO();
        userVO.setPassword(null); // password값을 저장해서는 안된다.
        userVO.setEmail(email);
        userVO.setNickname("Nickname");
        userVO.setFirstName("FirstName");
        userVO.setLastName("LastName");

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.name()));

        return new UsernamePasswordAuthenticationToken(userVO, null, authorities);
/*
        UserVO userVO = userMapper.getUserByEmail(email);
        if (userVO != null) {
            if (!passwordEncoder.matches(password, userVO.getPassword()))
                throw new BadCredentialsException("login.invalid-password"); // exception message에 messages.properties의 키 값을 넣으면 login.html 템플릿에서 알아서 localized 메시지를 보여준다.

            List<String> userRole = userMapper.getUserRoleByUid(userVO.getUid());
            if (userRole == null)
                throw new BadCredentialsException("login.no-authority");
            userVO.setPassword(null);

            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (String role : userRole)
                authorities.add(new SimpleGrantedAuthority(role));
            return new UsernamePasswordAuthenticationToken(userVO, null, authorities);
        }

        throw new BadCredentialsException("login.no-user-found");
 */
    }

    public boolean isUniqueEmail(String email) {
        return (userMapper.getCount("User", "email", email) <= 0);
    }

    public boolean isUniqueNickname(String nickname) {
        return (userMapper.getCount("User", "nickname", nickname) <= 0);
    }
}
