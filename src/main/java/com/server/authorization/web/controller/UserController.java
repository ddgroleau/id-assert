package com.server.authorization.web.controller;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.application.viewmodel.CreateUserViewModel;
import com.server.authorization.application.viewmodel.ForgotPasswordViewModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
        private final AppUserService appUserService;
        public UserController(@Qualifier("userDetailsService") AppUserService appUserService) {
            this.appUserService = appUserService;
        }
        @GetMapping("/sign-up")
        public String signUp(CreateUserViewModel createUserViewModel) {
            return "sign-up";
        }

        @GetMapping("/forgot-password")
        public String forgotPassword(ForgotPasswordViewModel forgotPasswordViewModel) {
            return "forgot-password";
        }

        @PostMapping("/create")
        public String createUser(@Valid CreateUserViewModel createUserViewModel, BindingResult result, Model model) {
            try {
                if (result.hasErrors()) return "sign-up";

                appUserService.createUser(createUserViewModel);
                return "redirect:/login";
            }
            catch (Exception e) {
                String err = e.getMessage();
                result.addError(new ObjectError("globalError",err));
                return "sign-up";
            }
        }

        @PostMapping("/send-reset-password-link")
        public String sendResetPasswordLink(@Valid ForgotPasswordViewModel forgotPasswordViewModel, BindingResult result, Model model) {
            try {
                if (result.hasErrors()) return "forgot-password";

                AppUser user = appUserService.findUserByEmail(forgotPasswordViewModel.getEmail());

                PasswordResetToken resetToken = appUserService.createPasswordResetTokenForUser(user, UUID.randomUUID().toString());
                appUserService.sendPasswordResetEmail(user, resetToken);

                return "redirect:/forgot-password?success";
            } catch (Exception e) {
                String err = e.getMessage();
                result.addError(new ObjectError("globalError",err));
                return "redirect:/forgot-password";
            }
        }

}
