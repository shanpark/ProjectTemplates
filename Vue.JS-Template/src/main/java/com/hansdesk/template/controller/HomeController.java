package com.hansdesk.template.controller;

import com.hansdesk.template.dto.AuthResDto;
import com.hansdesk.template.dto.HomeReqDto;
import com.hansdesk.template.dto.HomeResDto;
import com.hansdesk.template.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    /**
     * 이 컨트롤러 메소드는 Vue.js로 생성된 index.html 파일을 내려주기 위한 것이며 permitAll이어야 한다.
     */
    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    /**
     * 이 컨트롤러를 호출할 수 있다는 것은 이미 인증 절차를 마친 세션이라는 뜻이므로 추가 절치는 필요하지 않고 정해진 응답을 해주면 된다.
     */
    @GetMapping("/reauth")
    @ResponseBody
    public AuthResDto reauth(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthResDto authResDto = new AuthResDto();
        UserVo userVo = (UserVo) authentication.getPrincipal();
        authResDto.setCode(0); // 0 = 성공
        authResDto.setEmail(userVo.getEmail());
        authResDto.setNickname(userVo.getNickname());
        return authResDto;
    }

    /**
     * 인증이 성공적으로 되는지 테스트하기 위한 컨트롤러이다.
     */
    @PostMapping("/home")
    @ResponseBody
    public HomeResDto home(@RequestBody HomeReqDto homeReqDto) {
        HomeResDto homeResDto = new HomeResDto();
        homeResDto.setMessage("Received:" + homeReqDto.getMessage());
        return homeResDto;
    }
}