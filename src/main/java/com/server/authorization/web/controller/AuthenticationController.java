package com.server.authorization.web.controller;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.viewmodel.LoginViewModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/login")
public class AuthenticationController {

    @GetMapping()
    public String login(LoginViewModel loginViewModel, Authentication authentication) {
        if(authentication != null && authentication.isAuthenticated()) return "redirect:/";
        return "login";
    }

    @GetMapping("/oauth2/code/id-assert-client-oidc")
    public String getJsonWebToken(@RequestParam String code) {
//        return "redirect:/authorized";
        return "";

    }

}
