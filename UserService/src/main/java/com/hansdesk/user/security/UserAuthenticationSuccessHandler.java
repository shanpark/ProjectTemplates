package com.hansdesk.user.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    // SavedRequestAwareAuthenticationSuccessHandler클래스를 상속해야 로그인 성공 후 원래 가려고 했던 페이지로 이동한다.
    // success handler를 지정하지 않고 default success url을 지정했을 때 이 클래스가 사용된다.

    UserAuthenticationSuccessHandler(String defaultSuccessUrl) {
        setDefaultTargetUrl(defaultSuccessUrl);
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws ServletException, IOException {

        // 로그인 성공 시 쿠키를 저장하기 위해서 success handler를 사용한다.
        String saveId = request.getParameter("saveId");
        String savedId = request.getParameter("email");
        if (saveId != null) { // checkbox가 check되면 "true", 아니면 null이 온다.
            Cookie saveIdCookie = new Cookie("saveId", saveId);
            saveIdCookie.setMaxAge(60 * 60 * 24 * 14); // 기간을 2주로 지정
            response.addCookie(saveIdCookie);

            Cookie savedIdCookie = new Cookie("savedId", savedId);
            savedIdCookie.setMaxAge(60 * 60 * 24 * 14); // 기간을 2주로 지정
            response.addCookie(savedIdCookie);
        } else {
            Cookie saveIdCookie = new Cookie("saveId", null);
            saveIdCookie.setMaxAge(0); // 즉시 삭제.
            response.addCookie(saveIdCookie);

            Cookie savedIdCookie = new Cookie("savedId", null);
            savedIdCookie.setMaxAge(0); // 즉시 삭제.
            response.addCookie(savedIdCookie);
        }

        super.onAuthenticationSuccess(request, response, authentication); // 반드시 호출해 주어야 계속 진행된다.
    }
}
