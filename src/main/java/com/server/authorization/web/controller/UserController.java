package com.server.authorization.web.controller;

import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.web.viewmodel.CreateUserViewModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {
        private final AppUserService appUserService;
        public UserController(@Qualifier("userDetailsService") AppUserService appUserService) {
            this.appUserService = appUserService;
        }
        @GetMapping("sign-up")
        public String signUp(CreateUserViewModel createUserViewModel) {
            return "sign-up";
        }

        @PostMapping("/create")
        public String createUser(@Valid CreateUserViewModel createUserViewModel, BindingResult result, Model model) {
            try {
                if (result.hasErrors()) {
                    return "sign-up";
                }
                appUserService.createUser(createUserViewModel);
                return "redirect:/login";
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return "sign-up";
            }
        }

}
