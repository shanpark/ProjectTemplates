package com.hansdesk.rest.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 인증을 요구하는 모든 경로에 대해서 적용되는 필터이다. Authorization 헤더를 파싱하여 JWT를 추출하고 유효한 JWT 토큰인지 검증한다.
 * 검증이 실패하면 401 Unauthorized 코드를 내려보낸다.
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            Authentication authentication = JWTHandler.getAuthenticationFromHeader((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | IllegalArgumentException e) {
            // JWT 관련 exception들이다. 따라서 401 Unauthorized로 응답한다.
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러코드 지정.

            // 에러 코드가 있으므로 Body가 꼭 필요한 건 아니지만 샘플로서 아래와 같이 Body를 보낼 수 있다.
            PrintWriter res = response.getWriter();
            res.printf("{\"message\": \"%s\"}", "Invalid JWT.");
        }
    }
}
