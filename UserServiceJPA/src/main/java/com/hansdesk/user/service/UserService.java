package com.hansdesk.user.service;

import com.hansdesk.user.constant.Role;
import com.hansdesk.user.constant.UserStatus;
import com.hansdesk.user.domain.SignUpUser;
import com.hansdesk.user.domain.User;
import com.hansdesk.user.dto.SignUpForm;
import com.hansdesk.user.dto.SignUpNextForm;
import com.hansdesk.user.repository.SignUpUserRepository;
import com.hansdesk.user.repository.UserRepository;
import com.hansdesk.user.security.UserPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SignUpUserRepository signUpUserRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private UserPasswordEncoder passwordEncoder;

    public boolean registerSignUpUser(SignUpForm signUpForm) {
        try {
            String hashed = passwordEncoder.encode(signUpForm.getPassword());

            SignUpUser signUpUser = new SignUpUser();
            signUpUser.setEmail(signUpForm.getEmail());
            signUpUser.setPassword(hashed);
            signUpUser.setFirstName(signUpForm.getFirstName());
            signUpUser.setLastName(signUpForm.getLastName());
            signUpUser.setNickname(signUpForm.getNickname());
            signUpUser.setModified(new Date()); // JPA 로는 DB의 NOW()를 이용할 수 없었음.

            signUpUserRepository.save(signUpUser);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public SignUpUser getSignUpUser(String email) {
        return signUpUserRepository.getOne(email);
    }

    public boolean registerUser(SignUpUser signUpUser, SignUpNextForm signUpNextForm, Role role) {
        try {
            User user = new User();
            user.setEmail(signUpUser.getEmail());
            user.setPassword(signUpUser.getPassword());
            user.setNickname(signUpUser.getNickname());
            user.setFirstName(signUpUser.getFirstName());
            user.setLastName(signUpUser.getLastName());
            user.setHp(signUpNextForm.getHp());
            user.setBirth(signUpNextForm.getBirth());
            user.setStatus(UserStatus.ACTIVE);
            user.getAuthorities().add(role);
            // modified, created 필드는 Date형이다. DB의 default 값을 NOW()로 설정하고
            // @Column 어노테이션에서 insertable=false를 설정하여
            // INSERT할 때는 DB의 NOW()를 이용할 수 있지만
            // UPDATE할 때는 DB의 NOW()를 이용할 수 없었다.
            // 결국 DB의 시간이 아닌 Local의 시간을 전달하거나, 다른 방법으로 DB의 시간을 조회해서 전달해주어야 한다.

            userRepository.save(user);

            signUpUserRepository.deleteById(signUpUser.getEmail());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (!passwordEncoder.matches(password, user.getPassword()))
                throw new BadCredentialsException("login.invalid-password"); // exception message에 messages.properties의 키 값을 넣으면 login.html 템플릿에서 알아서 localized 메시지를 보여준다.

            if (user.getAuthorities().isEmpty())
                throw new BadCredentialsException("login.no-authority");

            ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (Role role : user.getAuthorities())
                authorities.add(new SimpleGrantedAuthority(role.name()));

            user.setPassword(null);
            em.detach(user); // password를 null로 설정했기 때문에 DB에 반영이 되지 않도록 context에서 제거해주어야 한다.
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        }

        throw new BadCredentialsException("login.no-user-found");
    }

    public boolean isUniqueEmail(String email) {
        return (userRepository.findByEmail(email) == null);
    }

    public boolean isUniqueNickname(String nickname) {
        return (userRepository.findByNickname(nickname) == null);
    }
}
