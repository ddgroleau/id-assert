package com.server.authorization.web.controller;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.dto.EventResponseDto;
import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.application.viewmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
        private static final Logger log = LoggerFactory.getLogger(UserController.class);

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
                log.error("UserController:createUser(): Exception thrown: " + e.getMessage());
                result.addError(new ObjectError("globalError","Could not create account. Please try again."));
                return "sign-up";
            }
        }

        @PostMapping("/send-reset-password-link")
        public String sendResetPasswordLink(@Valid ForgotPasswordViewModel forgotPasswordViewModel, BindingResult result, Model model) {
            try {
                if (result.hasErrors()) return "forgot-password";

                AppUser user = appUserService.findUserByEmail(forgotPasswordViewModel.getEmail());
                if(user == null || !user.isActive()) {
                    log.error("UserController:sendResetPasswordLink(): No user found with email: " + forgotPasswordViewModel.getEmail());
                    return "redirect:/user/forgot-password?success";
                }

                PasswordResetToken resetToken = appUserService.createPasswordResetTokenForUser(user, UUID.randomUUID().toString());

                appUserService.sendPasswordResetEmail(user, resetToken);

                return "redirect:/user/forgot-password?success";
            } catch (Exception e) {
                log.error("UserController:sendResetPasswordLink(): Exception thrown: " + e.getMessage());
                result.addError(new ObjectError("globalError","Could not send reset password link. Please try again."));
                return "redirect:/user/forgot-password?error";
            }
        }

        @GetMapping("/reset-password/{userId}/{token}")
        public String resetPassword(
                @PathVariable String userId,
                @PathVariable String token,
                ResetPasswordViewModel resetPasswordViewModel,
                HttpSession session) {

            EventResponseDto eventResponseDto = appUserService.validatePasswordResetToken(userId,token);
            log.info("UserController:resetPassword(): Token Validation: isSuccess: " + eventResponseDto.isSuccess() + " message: " + eventResponseDto.getMessage());

            session.setAttribute("isValid",eventResponseDto.isSuccess());
            session.setAttribute("message",eventResponseDto.getMessage());

            if(eventResponseDto.isSuccess()) resetPasswordViewModel.setUserId(userId);

            return "reset-password";
        }

    @PostMapping("/reset-password")
    public String resetPassword(@Valid ResetPasswordViewModel resetPasswordViewModel,
                               BindingResult result,
                               Model model
    ) {
        try {
            log.info("UserController:changePassword(): Request to change password for user with Id: " + resetPasswordViewModel.getUserId());
            appUserService.changeUserPassword(resetPasswordViewModel.getUserId(),resetPasswordViewModel.getNewPassword());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("UserController:sendResetPasswordLink(): Exception thrown: " + e.getMessage());
            result.addError(new ObjectError("globalError","Could not reset password. Please try again."));
            return "redirect:/user/reset-password";
        }
    }

    @GetMapping("/account")
    public String getAccountInfo(ProfileViewModel profileViewModel, ChangePasswordViewModel changePasswordViewModel, HttpSession session) {
            return "account";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid ProfileViewModel profileViewModel,
                                BindingResult result,
                                HttpSession session
    ) {
        try {
            session.setAttribute("isSuccess",true);
            session.setAttribute("profileMessage","Profile information was updated successfully.");
            return "redirect:/user/account";
        } catch (Exception e) {
            log.error("UserController:updateProfile(): Exception thrown: " + e.getMessage());
            session.setAttribute("isSuccess",false);
            session.setAttribute("profileMessage","Could not update profile information. Please try again.");
            return "redirect:/user/account";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid ChangePasswordViewModel changePasswordViewModel,
                                BindingResult result,
                                 HttpSession session
    ) {
        try {
            session.setAttribute("isSuccess",true);
            session.setAttribute("passwordMessage","Password was updated successfully.");
            return "redirect:/user/account";
        } catch (Exception e) {
            log.error("UserController:changePassword(): Exception thrown: " + e.getMessage());
            session.setAttribute("isSuccess",false);
            session.setAttribute("passwordMessage","Could not change password. Please try again.");
            return "redirect:/user/account";
        }
    }

}
