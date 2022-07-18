package com.server.authorization.application.service.implementation;

import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.dto.EventResponseDto;
import com.server.authorization.application.pojo.MessageMediaTypes;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.dto.MessageDto;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.application.repository.abstraction.PasswordTokenRepository;
import com.server.authorization.application.service.abstraction.MessageAdapter;
import com.server.authorization.application.viewmodel.CreateUserViewModel;
import com.server.authorization.application.viewmodel.UpdateProfileViewModel;
import com.server.authorization.web.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.persistence.EntityExistsException;
import javax.validation.constraints.Email;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.*;

import static com.server.authorization.web.configuration.AuthorizationServerConfiguration.passwordEncoder;

@Service("userDetailsService")
public class AppUserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(AppUserService.class);
    private String identityBaseUri;
    private AppUserRepository appUserRepository;

    @Qualifier("emailClient")
    private MessageAdapter emailClient;
    private PasswordTokenRepository passwordTokenRepository;

    public AppUserService(
            AppUserRepository appUserRepository,
            PasswordTokenRepository passwordTokenRepository,
            MessageAdapter emailClient,
            @Value("${identity.base.uri}") String identityBaseUri
            ) {
        this.appUserRepository = appUserRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailClient = emailClient;
        this.identityBaseUri = identityBaseUri;
    }

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }

    public void createUser(CreateUserViewModel createUserViewModel) {
        if(createUserViewModel == null) throw new InvalidParameterException("New user is required.");

        AppUser appUser = appUserRepository.findByUsername(createUserViewModel.getEmail());
        if(appUser != null) throw new InvalidParameterException("Username not available.");

        this.appUserRepository.saveAndFlush(AppUser.createNewUser(
                createUserViewModel.getEmail(),
                createUserViewModel.getFirstName(),
                createUserViewModel.getLastName(),
                createUserViewModel.getPassword()
        ));
        log.info("AppUserService:createUser(): New user created with email: " + createUserViewModel.getEmail());
        return;
    }

    public AppUser findUserByEmail(String email) {
        if(email == null || email.isEmpty()) throw new InvalidParameterException("Email is required");

        AppUser appUser = appUserRepository.findByEmail(email);
        if(appUser == null) throw new InvalidParameterException("User does not exist.");

        return appUser;
    }

    public PasswordResetToken createPasswordResetTokenForUser(AppUser appUser, String token) {
        if(appUser == null || token == null || token.isEmpty())
            throw new InvalidParameterException("User and token are required.");

        Set<PasswordResetToken> activeTokens = passwordTokenRepository.findAllByAppUser_UserId(appUser.getUserId());
        if(!activeTokens.isEmpty()) {
            log.info("AppUserService:createPasswordResetTokenForUser(): Deleting active tokens for: " + appUser.getEmail());
            passwordTokenRepository.deleteAll(activeTokens);
        }

        PasswordResetToken resetToken = PasswordResetToken.generateToken(token, appUser);
        passwordTokenRepository.saveAndFlush(resetToken);
        log.info("AppUserService:createPasswordResetTokenForUser(): Reset password token generated for " + appUser.getEmail());

        return resetToken;
    }

    public void sendPasswordResetEmail(AppUser appUser, PasswordResetToken resetToken) throws IOException, MessagingException {
        if(appUser == null || resetToken == null) throw new InvalidParameterException("User and token are required.");

        String resetPasswordEndpoint = String.format(
                "%1$s/user/reset-password/%2$s/%3$s",
                identityBaseUri,
                appUser.getUserId(),
                resetToken.getToken()
        );
        HashMap<String,String> templateVariables = new HashMap<>(){{
            put("~RESET_LINK~",resetPasswordEndpoint);
            put("~SERVER_URL~",identityBaseUri);
        }};

        String emailHtml = EmailClient.getHtmlEmailTemplate("templates/email/forgot-password-message.html",templateVariables);

        MessageDto messageDto = MessageDto.createMessage(
                appUser.getEmail(),
                "Change Your Root to Rise Password",
                emailHtml,
                MessageMediaTypes.HTML.ordinal()
        );

        emailClient.sendMessage(messageDto);
        log.info("AppUserService:sendPasswordResetEmail(): Reset password link sent to " + appUser.getEmail() + " at " + resetPasswordEndpoint);
        return;
    }

    public EventResponseDto validatePasswordResetToken(String userId, String token) {
        if(userId == null || userId.isEmpty() || token == null || token.isEmpty())
            throw new InvalidParameterException("UserId and Token are required.");

        PasswordResetToken resetToken = passwordTokenRepository.findByToken(token);
        if(resetToken == null || !resetToken.getAppUser().getUserId().equals(userId))
            return EventResponseDto.createResponse(false,"Invalid password reset token.");

        if(resetToken.getExpiryDate().isBefore(LocalDateTime.now()))
            return EventResponseDto.createResponse(false,"Your password reset token has expired.");

        passwordTokenRepository.delete(resetToken);
        return EventResponseDto.createResponse(true,"Please reset your password below.");
    }
    public void changeUserPassword(String userId, String newPassword) {
        if(userId == null || userId.isEmpty() || newPassword == null || newPassword.isEmpty())
            throw new InvalidParameterException("UserId and New Password are required.");

        Optional<AppUser> appUser = appUserRepository.findById(userId);
        if(appUser.isEmpty()) throw new UsernameNotFoundException("User does not exist.");

        AppUser userEntity = appUser.get();

        userEntity.setPassword(newPassword);
        appUserRepository.save(userEntity);
        log.info("AppUserService:changeUserPassword(): Password was reset for " + userEntity.getEmail());
    }

    public boolean matchesCurrentPassword(String userId, String currentPassword) {
        if(userId == null || userId.isEmpty() || currentPassword == null || currentPassword.isEmpty())
            throw new InvalidParameterException("UserId and Current Password are required.");

        Optional<AppUser> appUser = appUserRepository.findById(userId);
        if(appUser.isEmpty()) throw new UsernameNotFoundException("User does not exist.");

        return passwordEncoder().matches(currentPassword,appUser.get().getPassword());
    }

    public void updateProfile(UpdateProfileViewModel updateProfileViewModel) {
        if(updateProfileViewModel == null ) throw new InvalidParameterException("User update information is required.");

        Optional<AppUser> appUser = appUserRepository.findById(updateProfileViewModel.getUserId());
        if(appUser.isEmpty()) throw new UsernameNotFoundException("User does not exist.");

        AppUser user = appUser.get();

        if(updateProfileViewModel.getFirstName() != null && !updateProfileViewModel.getFirstName().isEmpty())
            user.setFirstName(updateProfileViewModel.getFirstName());
        if(updateProfileViewModel.getLastName() != null && !updateProfileViewModel.getLastName().isEmpty())
            user.setLastName(updateProfileViewModel.getLastName());
        if(updateProfileViewModel.getPhone() != null && !updateProfileViewModel.getPhone().isEmpty())
            user.setPhone(updateProfileViewModel.getPhone());

        if(updateProfileViewModel.getEmail() != null && !updateProfileViewModel.getEmail().isEmpty()) {
            AppUser existingUser = appUserRepository.findByEmail(updateProfileViewModel.getEmail());
            if(existingUser != null && !existingUser.getUserId().equals(updateProfileViewModel.getUserId()))
                throw new EntityExistsException("A user already exists with that email.");
            if (existingUser == null) {
                user.setEmail(updateProfileViewModel.getEmail());
                user.setUsername(updateProfileViewModel.getEmail());
            }
        }

        appUserRepository.save(user);
    }
}