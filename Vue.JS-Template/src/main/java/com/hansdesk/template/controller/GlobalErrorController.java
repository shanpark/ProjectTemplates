package com.hansdesk.template.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Boot에 기본적으로 ErrorController 구현체가 있지만 아래와 같이 사용자 ErrorController 구현체를 추가해주면 우선적으로
 * 추가된 사용자 ErrorController 구현체가 사용된다.
 */
@Controller
public class GlobalErrorController implements ErrorController {

    /**
     * 기본적으로 '/error'가 error path로 설정되어 있으므로 아래와 같이 선언해준다.
     * 에러가 발생하면 '/'로 리다이렉트 시킨다. 주소창에 아무 주소나 입력해서 생긱는 404 에러를 막기 위함이다.
     * 일반적인 Controller의 RequestMapping을 구현하는 것과 같다. 따라서 View 템플릿 이름을 반환해도 되고
     * 아래 구현처럼 redirect url을 반환하는 것도 모두 가능하다.
     * 404만 리다이렉트 시키기 위해서 좀 더 상세한 처리를 할 수도 있으므로 필요하다면 구글링해볼 것.
     */
    @RequestMapping("/error")
    public String handleError() {
        return "redirect:/";
    }

    /**
     * ErrorContoller를 구현해야 하기 때문에 함수를 정의만 해 놓는다.
     * 2.3.0 이후로 `server.error.path` 설정값을 사용하며 이 함수의 리턴값은 사용되지 않는다.
     */
    @Override
    public String getErrorPath() {
        return null;
    }
}
