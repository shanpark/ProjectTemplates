package com.hansdesk.template.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 인증을 요구하는 모든 경로에 대해서 적용되는 필터이다. Authorization 헤더를 파싱하여 JWT를 추출하고 유효한 JWT 토큰인지 검증한다.
 * 검증이 실패하면 401 Unauthorized 코드를 내려보낸다.
 * 실패 시 Body는 없지만 만들어서 내려보낼 수 있다. (RestService Template 참조)
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication;
        try {
            authentication = JwtHandler.getAuthenticationFromCookie((HttpServletRequest) request);
        } catch (Exception ignored) {
            authentication = null;
        }

        // 여기까지 왔을 때
        //   정상적인 JWT가 존재한다면 Authentication이 정상적으로 생성되었을 것이고
        //   그렇지 않다면 authentication이 null일 것이다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // SecurityContext에 Authentication이 설정되어 있지 않으면 이후의 필터에서 통과가 되지 않고 Forbidden이 반환될 것이다.
        chain.doFilter(request, response);
    }
}