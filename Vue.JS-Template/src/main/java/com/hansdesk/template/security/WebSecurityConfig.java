package com.hansdesk.template.security;

import com.hansdesk.template.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

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

        // Vue.js가 생성하는 파일들에 대한 요청은 모두 허가해줘야 한다. 정적 데이터이므로 모두 허가해줘도 문제가 없다.
        // '/auth' 요청은 모두 허용이고 나머지 REST api들은 인증 사용자만 access 가능하도록 설정한다.
        http.authorizeRequests()
            .antMatchers(HttpMethod.GET, "/", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll() // Vue.JS가 생성하는 파일에 대한 요청은 모두 허가해 주어야 한다.
            .antMatchers(HttpMethod.POST, "/auth").permitAll() // /auth 는 jwt를 발급받기 위한 url이다.
            .anyRequest().authenticated();

        // '/auth' 요청에 대한 필터 설정. 아래 JWTAuthenticationFilter보다 먼저 추가해야 한다.
        // 순서가 바뀌면 /login 경로에 대해서 JWTAuthenticationFilter보다 필터가 먼저 적용되므로 오동작의 우려가 있다.
        http.addFilterBefore(new JwtLoginFilter("/auth", userService), UsernamePasswordAuthenticationFilter.class);

        // 나머지 요청에 대한 필터 설정.
        http.addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 아래 핸들러들은 Vue.js의 router를 거치지 않고 브라우저의 주소창에서 직접 주소를 입력해서 요청이 들어온 경우를 처리하기 위해서 지정하는 것이다.
        //  1. 인증받지 않은 상태에서 주소창에 아무 경로나 입력하고 요청할 수도 있고
        //  2. 인증을 받은 상태에서 주소창에 아무 경로나 입력하고 요청할 수도 있다.
        // 1번의 경우 어떤 요청이라도 모두 앱의 '/' 경로로 리다이렉트 시키면 Vue.js에서 인증 상태에 따라서 동작을 할 것이다. 브라우저에서 404가 보이면
        // 안되기 때문에 이렇게 처리한다. 아래 핸들러를 추가하는 이유이다.
        // 2번의 경우에도 컨트롤러의 mapping이 없는 경우에는 사용자가 임의로 입력한 것이라고 판단하여 '/' 경로로 리다이렉트 시켜야 한다. 마찬가지로
        // Vue.js에서 인증 상태를 파악하여 적당한 페이지를 보여줄 것이다. 역시 404가 보이지 않도록 하기 위함이다. 404를 처리할 필요가 있다.
        // 여기서는 GlobalErrorController를 구현하고 있으므로 참고.
        //
        // * Vue.js에서는 새로고침 등으로 $store에 사용자 정보가 없는 상태에서 임의의 데이터를 요청하게 될 경우 인증 상태 여부를 판단하기 위해서
        //   '/reauth'를 요청하여 사용자 정보를 받아와서 다시 $store에 사용자 정보를 저장한다. 따라서 /reauth의 경우 인증되지 않은 상태에서 요청이
        //   들어오면 redirect를 시키면 안되고 403 Forbidden을 내려줘야 한다. 왜냐하면 302 Redirect도 결국 다음 경로를 요청하면서 성공처리
        //   되기 때문에 사용자 정보가 내려가지 않았음에도 요청이 성공이라고 판단해버리기 때문이다.
        // * JWT가 httpOnly Cookie에 저장되어 내려가기 때문에 Vue.js에서는 JWT가 Cookie에 있는 지 없는 지 판단할 수 없다. 즉 인증 상태를
        //   판단할 수 없는 것이다. 결국 그냥 요청을 해보고 실패한다면 인증받지 않은 것으로 판단할 수 밖에 없다.
        //   결국 로그인 페이지 처럼 요청할 게 없는 페이지의 경우 판단할 방법이 없고 인증 상태에 따라 다른 동작을 해야 한다면 '/reauth'를 요청해서
        //   성공 여부에 따라 인증 상태를 판단한다. 하지만 요청이 많아질 수 있으므로 $store에 사용자 인증 정보를 저장해 놓으면 사용자가 새로고침을
        //   하거나 새로운 탭을 열기 전에는 $store에 있는 정보를 기준으로 인증 상태를 판단하고 인증 정보가 없는 경우에만 '/reauth'를 요청해서
        //   인증 상태를 파악하도록 한다.

        // 인증받지 않은 상태에서 허가되지 않은 요청(403 에러)이 들어오면 아래 핸들러가 호출될 것이다. (위 주석에서 1번의 처리를 위해 등록한다.)
        http.exceptionHandling().authenticationEntryPoint(((request, response, authException) -> {
            if (request.getServletPath().equals("/reauth")) // reauth의 경우에는 redirect를 시키면 안되고 Forbidden을 내려줘야 한다. 그래야 클라이언트에서 인증되지 않은 상태인 걸 인지하고 로그인 페이지로 route할 것이다.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            else
                response.sendRedirect("/");
        }));
    }
}
