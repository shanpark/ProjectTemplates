package com.hansdesk.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansdesk.user.constant.Role;
import com.hansdesk.user.dto.LoginForm;
import com.hansdesk.user.dto.SignUpForm;
import com.hansdesk.user.dto.SignUpNextForm;
import com.hansdesk.user.service.UserService;
import com.hansdesk.user.util.Utility;
import com.hansdesk.user.vo.SignUpUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/sign-up")
    public String signUp(@ModelAttribute("signUpForm") SignUpForm signUpForm, Model model) {
        // 이미 로그인 상태이면 /home으로 redirect 시킨다.
        if (Utility.isAuthenticated())
            return "redirect:/home";

        return "sign-up";
    }

    @PostMapping("/sign-up")
    public String postSignUp(@ModelAttribute("signUpForm") @Valid SignUpForm signUpForm, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // 이렇게 코드로 구현하면 기본적인 검증은 Annotation으로 해결하고 추가 검증이 필요한 것들만 간단하기에 여기서 처리가 가능하다.
        if (!signUpForm.getPasswordConfirm().equals(signUpForm.getPassword()))
            bindingResult.rejectValue("passwordConfirm", "Same", "The second password is different from the first one.");

        if (bindingResult.hasErrors())
            return "sign-up";

        if (!userService.registerSignUpUser(signUpForm))
            return "sign-up?error=sign-up.unknown-err";

        // 이렇게 redirectAttributes에 넣어놓으면 redirect되는 컨트롤러 메소드에서 같은 이름으로 받을 수 있다.
        redirectAttributes.addFlashAttribute("signUpForm", signUpForm);
        return "redirect:/sign-up-next";
    }

    @GetMapping("/sign-up-next")
    public String signUpNext(@ModelAttribute("signUpNextForm") SignUpNextForm signUpNextForm, SignUpForm signUpForm, Model model) {
        if (signUpForm.getEmail() == null) // sign-up 페이지를 거쳐서 온게 아니면 다시 sign-up 페이지로 보낸다.
            return "redirect:/sign-up";

        signUpNextForm.setEmail(signUpForm.getEmail());
        return "sign-up-next";
    }

    @PostMapping("/sign-up-next")
    public String postSignUpNext(@ModelAttribute("signUpNextForm") @Valid SignUpNextForm signUpNextForm, BindingResult bindingResult, Model model) {
        SignUpUserVO signUpUserVO = userService.getSignUpUser(signUpNextForm.getEmail());
        if (signUpUserVO == null)
            return "redirect:/sign-up?error=sign-up.unknown-err";

        if (bindingResult.hasErrors())
            return "sign-up-next";

        if (!userService.registerUser(signUpUserVO, signUpNextForm, Role.ROLE_USER))
            return "redirect:/sign-up?error=sign-up.reg-err";

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm loginForm, Model model) {
        // 이미 로그인 상태이면 /home으로 redirect 시킨다.
        if (Utility.isAuthenticated())
            return "redirect:/home";

        return "login";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }

    @PostMapping("/email-dup-check")
    @ResponseBody
    public String emailDupCheck(@RequestParam String data) {
        Map<String, Object> response = new HashMap<>();

        if (userService.isUniqueEmail(data)) {
            response.put("success", true);
            response.put("message", messageSource.getMessage("email-dup-check.can-use", null, LocaleContextHolder.getLocale()));
        } else {
            response.put("success", false);
            response.put("message", messageSource.getMessage("email-dup-check.duplicate", null, LocaleContextHolder.getLocale()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"success\":false," +
                    "\"message\":" +  messageSource.getMessage("email-dup-check.unknown-err", null, LocaleContextHolder.getLocale()) +
                    "}";
        }
    }

    @PostMapping("/nickname-dup-check")
    @ResponseBody
    public String nicknameDupCheck(@RequestParam String data) {
        Map<String, Object> response = new HashMap<>();

        if (userService.isUniqueNickname(data)) {
            response.put("success", true);
            response.put("message", messageSource.getMessage("nickname-dup-check.can-use", null, LocaleContextHolder.getLocale()));
        } else {
            response.put("success", false);
            response.put("message", messageSource.getMessage("nickname-dup-check.duplicate", null, LocaleContextHolder.getLocale()));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            return "{\"success\":false," +
                    "\"message\":" +  messageSource.getMessage("nickname-dup-check.unknown-err", null, LocaleContextHolder.getLocale()) +
                    "}";
        }
    }}
