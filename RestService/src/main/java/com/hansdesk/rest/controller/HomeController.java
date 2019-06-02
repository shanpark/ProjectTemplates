package com.hansdesk.rest.controller;

import com.hansdesk.rest.dto.RequestDto;
import com.hansdesk.rest.dto.ResponseDto;
import com.hansdesk.rest.vo.UserVO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@RestController
public class HomeController {

    /**
     * JSON 형태의 요청과 응답을 처리하는 예제 Controller 구현이다.
     * 요청과 응답 모드 JSON 형태로 이루어지지만 이 메소드는 JSON을 자바 객체로 변환하는 변환기가 자동으로 동작하여 요청과 반환을 모두
     * 자바 객체로 수행하는 예를 볼 수 있다.
     *
     * @param requestDto {"data":"hello"} 과 같은 JSON 형태의 요청을 받아서 RequestDto 객체로 받는다.
     * @return ResponseDto 객체를 반환하면 {"data":"hello", "response":"OK"} 형태의 JSON 포맷 응답을 내려보낸다.
     */
    @PostMapping("/home")
    public ResponseDto home(@RequestBody RequestDto requestDto) {
        UserVO userVO = (UserVO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ResponseDto responseDto = new ResponseDto("OK");
        responseDto.setData(requestDto.getData());

        return responseDto;
    }
}
