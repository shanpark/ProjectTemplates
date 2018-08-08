package com.hansdesk.rest.controller;

import com.hansdesk.rest.vo.UserVO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        UserVO userVO = (UserVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "{\"uid\": " + userVO.getUid() + ", \"email\": \"" + userVO.getEmail() + "\", \"nickname\": \"" + userVO.getNickname() + "\"}";
    }
}
