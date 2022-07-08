package com.server.authorization.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/login")
public class AuthenticationController {

    @GetMapping()
    public String login() {
        return "login";
    }

    @GetMapping("/oauth2/code/id-assert-client-oidc")
    public String getJsonWebToken(@RequestParam String code) {
        System.out.println(code);
        return "redirect:/authorized";
    }

}
