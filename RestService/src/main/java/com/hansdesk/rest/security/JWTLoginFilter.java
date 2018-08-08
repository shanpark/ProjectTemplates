package com.hansdesk.rest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansdesk.rest.dto.LoginDto;
import com.hansdesk.rest.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * /login 요청에 대한 필터.
 * 요청으로부터 email, password를 가져와서 인증을 수행하고 정상 사용자인 경우 JWT 토큰을 생성하여 Response 헤더에 JWT 토큰을 설정한다.
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private UserService userService;

    public JWTLoginFilter(String url, UserService userService) {
        super(new AntPathRequestMatcher(url));
        this.userService = userService; // Filter가 bean이 아니기 때문에 Autowired를 이용하지 않고 생성자를 통해서 초기화 해준다.
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // JSON 통신이며 Request의 Body는 아래 예와 같은 형태의 데이터가 올라온다고 가정한다.
        // {"email":"shanpark@naver.com", "password":"somepassword"}
        LoginDto loginDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);

        // DB에서 사용자 정보를 가져와서 비밀번호 인증을 수행한 수 적절한 Authentication 객체를 반환한다.
        return userService.authenticate(loginDto.getEmail(), loginDto.getPassword());
    }

    /**
     * 인증 성공 시 호출된다. JWT 토큰을 생성해서 응답 헤더에 추가해 준다. 이 함수가 호출되었다면 응답으로 200 OK가 내려간다는 것이다.
     * 아래 예와 같이 응답에 Body를 넣고싶다면 넣을 수 있다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        // JWT 토큰을 생성한다.
        String jwt = JWTHandler.generateJwtToken(authentication);

        // 생성된 토큰을 Response Header에 추가한다.
        JWTHandler.addJwtToResponseHeader(jwt, response);

        // Authorization헤더가 있고 200 OK 코드가 내려가므로 Body가 꼭 필요한 건 아니지만 샘플로서 아래와 같이 Body를 보낼 수 있다.
        PrintWriter res = response.getWriter();
        res.printf("{\"message\": \"%s\"}", "Login Success");
    }

    /**
     * 인증 실패 시(예외 발생 시) 호출된다. 인증이 실패하였으므로 SecurityContextHolder에서 Context를 clear해준다.
     * 응답 코드는 일반적으로 403 Forbidden으로 설정해준다. 다른 코드를 사용할 수도 있다.
     * 아래 예와 같이 응답에 Body를 넣고 싶다면 넣을 수 있다.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // 에러 코드가 있으므로 Body가 꼭 필요한 건 아니지만 샘플로서 아래와 같이 Body를 보낼 수 있다.
        PrintWriter res = response.getWriter();
        res.printf("{\"message\": \"%s\"}", "Login failed");
    }
}
