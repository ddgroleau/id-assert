package com.server.authorization.web.controller;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.dto.EventResponseDto;
import com.server.authorization.application.service.implementation.AppUserService;
import com.server.authorization.application.viewmodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityExistsException;
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
        if (result.hasErrors()) return "reset-password";
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
    public String getAccountInfo(UpdateProfileViewModel updateProfileViewModel,
                                 ChangePasswordViewModel changePasswordViewModel,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal AppUser appUser
    ) {
        try {
            AppUser user = appUserService.findUserByEmail(appUser.getEmail());

            appUser.setPhone(user.getPhone());
            appUser.setLastName(user.getLastName());
            appUser.setFirstName(user.getFirstName());
            appUser.setEmail(user.getEmail());
            appUser.setPassword(user.getPassword());

        } catch (Exception e) {
            log.error("UserController:getAccountInfo(): Exception thrown: " + e.getMessage());
        }
        finally {
            return "account";
        }
    }

    @PostMapping("/update-profile")
    public String updateProfile(@Valid UpdateProfileViewModel updateProfileViewModel,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                ChangePasswordViewModel changePasswordViewModel
    ) {
        if (result.hasErrors()) return "account";
        try {
            appUserService.updateProfile(updateProfileViewModel);
            EventResponseDto responseDto = EventResponseDto.createResponse(true,"Profile information was updated successfully.");
            redirectAttributes.addFlashAttribute("updateProfileResponse",responseDto);
        } catch (EntityExistsException e) {
            log.error("UserController:updateProfile(): Exception thrown: " + e.getMessage());
            EventResponseDto responseDto = EventResponseDto.createResponse(false,"A user with that email already exists.");
            redirectAttributes.addFlashAttribute("updateProfileResponse",responseDto);
        }
        catch (Exception e) {
            log.error("UserController:updateProfile(): Exception thrown: " + e.getMessage());
            EventResponseDto responseDto = EventResponseDto.createResponse(false,"Could not update profile information. Please try again.");
            redirectAttributes.addFlashAttribute("updateProfileResponse",responseDto);
        }
        finally {
            return "redirect:/user/account";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@Valid ChangePasswordViewModel changePasswordViewModel,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes,
                                 UpdateProfileViewModel updateProfileViewModel
                                 ) {
        if (result.hasErrors()) return "account";
        try {
            boolean confirmPassAndNewPassMatch =
                    changePasswordViewModel.getConfirmNewPassword().equals(changePasswordViewModel.getNewPassword());

            boolean currentPassIsValid = appUserService.matchesCurrentPassword(
                    changePasswordViewModel.getUserId(),
                    changePasswordViewModel.getCurrentPassword());

            if(confirmPassAndNewPassMatch && currentPassIsValid) {
                appUserService.changeUserPassword(changePasswordViewModel.getUserId(),changePasswordViewModel.getNewPassword());
                EventResponseDto changePasswordResponse = EventResponseDto.createResponse(true,"Password was updated successfully.");
                redirectAttributes.addFlashAttribute("changePasswordResponse",changePasswordResponse);
            } else {
                String message = confirmPassAndNewPassMatch ?
                        "The current password you entered is not valid." : "New password and confirm password do not match.";
                EventResponseDto changePasswordResponse = EventResponseDto.createResponse(false,message);
                redirectAttributes.addFlashAttribute("changePasswordResponse",changePasswordResponse);
            }
        } catch (Exception e) {
            log.error("UserController:changePassword(): Exception thrown: " + e.getMessage());
            EventResponseDto changePasswordResponse = EventResponseDto.createResponse(false,"Could not change password. Please try again.");
            redirectAttributes.addFlashAttribute("changePasswordResponse",changePasswordResponse);
        }
        finally {
            return "redirect:/user/account";
        }
    }

}
