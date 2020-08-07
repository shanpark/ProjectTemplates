package com.hansdesk.template.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansdesk.template.dto.AuthReqDto;
import com.hansdesk.template.dto.AuthResDto;
import com.hansdesk.template.service.UserService;
import com.hansdesk.template.vo.UserVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * /login 요청에 대한 필터.
 * 요청으로부터 email, password를 가져와서 인증을 수행하고 정상 사용자인 경우 JWT 토큰을 생성하여 Response 헤더에 JWT 토큰을 설정한다.
 */
public class JwtLoginFilter extends AbstractAuthenticationProcessingFilter  {

    private final UserService userService;

    public JwtLoginFilter(String url, UserService userService) {
        super(new AntPathRequestMatcher(url));
        this.userService = userService; // Filter가 bean이 아니기 때문에 Autowired를 이용하지 않고 생성자를 통해서 초기화 해준다.
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        // JSON 통신이며 Request의 Body는 아래 예와 같은 형태의 데이터가 올라온다고 가정한다.
        // {"email":"shanpark@naver.com", "password":"somepassword"}
        AuthReqDto authReqDto = new ObjectMapper().readValue(request.getInputStream(), AuthReqDto.class);

        // DB에서 사용자 정보를 가져와서 비밀번호 인증을 수행한 수 적절한 Authentication 객체를 반환한다.
        return userService.authenticate(authReqDto.getEmail(), authReqDto.getPassword());
    }

    /**
     * 인증 성공 시 호출된다. JWT 토큰을 생성해서 응답 헤더에 추가해 준다. 이 함수가 호출되었다면 응답으로 200 OK가 내려간다는 것이다.
     * 아래 예와 같이 응답에 Body를 넣고싶다면 넣을 수 있다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        // JWT 토큰을 생성한다.
        String jwt = JwtHandler.generateJwtToken(authentication);

        // 생성된 토큰을 httpOnly Cookie에 추가한다.
        JwtHandler.addJwtToResponseCookie(jwt, response);

        // Cookie에 JWT가 내려가고 200 OK 코드가 내려가므로 Body가 꼭 필요한 건 아니지만 샘플로서 아래와 같이 Body를 보낼 수 있다.
        UserVo userVo = (UserVo)authentication.getPrincipal();
        AuthResDto authResDto = new AuthResDto();
        authResDto.setCode(0); // 0 = 성공.
        authResDto.setEmail(userVo.getEmail());
        authResDto.setNickname(userVo.getNickname());

        // 정해진 정보를 json으로 내려준다. (클라이언트로 ReauthResDto 정보를 내려주기로 약속 되었다고 가정한다)
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writeValueAsString(authResDto);
        PrintWriter res = response.getWriter();
        res.print(jsonResult);
    }

    /**
     * 인증 실패 시(예외 발생 시) 호출된다. 인증이 실패하였으므로 SecurityContextHolder에서 Context를 clear해준다.
     * 응답 코드는 일반적으로 401 Unauthorized로 설정해준다. 다른 코드를 사용할 수도 있다.
     * 아래 예와 같이 응답에 Body를 넣고 싶다면 넣을 수 있다.
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // HTTP Status 코드가 있으므로 Body가 꼭 필요한 건 아니지만 샘플로서 아래와 같이 Body를 보낼 수 있다.
        AuthResDto authResDto = new AuthResDto();
        authResDto.setCode(-1); // -1 = 실패.

        // 정해진 정보를 json으로 내려준다. (클라이언트로 ReauthResDto 정보를 내려주기로 약속 되었다고 가정한다)
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writeValueAsString(authResDto);
        PrintWriter res = response.getWriter();
        res.print(jsonResult);
    }
}
