package com.server.authorization.application.service.implementation;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.pojo.MessageMediaTypes;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.dto.MessageDto;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.application.repository.abstraction.PasswordTokenRepository;
import com.server.authorization.application.service.abstraction.MessageAdapter;
import com.server.authorization.application.viewmodel.CreateUserViewModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

@Service("userDetailsService")
public class AppUserService implements UserDetailsService {

    @Value("${identity.base.uri}")
    private String identityBaseUri;
    private AppUserRepository appUserRepository;

    @Qualifier("emailClient")
    private MessageAdapter emailClient;
    private PasswordTokenRepository passwordTokenRepository;

    public AppUserService(AppUserRepository appUserRepository, PasswordTokenRepository passwordTokenRepository, MessageAdapter emailClient) {
        this.appUserRepository = appUserRepository;
        this.passwordTokenRepository = passwordTokenRepository;
        this.emailClient = emailClient;
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
        PasswordResetToken resetToken = PasswordResetToken.generateToken(token, appUser);
        passwordTokenRepository.saveAndFlush(resetToken);
        return resetToken;
    }

    public void sendPasswordResetEmail(AppUser appUser, PasswordResetToken resetToken) throws IOException, MessagingException {
        if(appUser == null || resetToken == null) throw new InvalidParameterException("User and token are required.");

        String resetPasswordEndpoint = String.format(
                "%1$s/reset-password/user/%2$s/token/%3$s",
                identityBaseUri,
                appUser.getUserId(),
                resetToken.getToken()
        );
        HashMap<String,String> templateVariables = new HashMap<>(){{put("[[RESET_LINK]]",resetPasswordEndpoint);}};

        String emailHtml = readHtmlTemplate("forgot-password-message.html",templateVariables);

        MessageDto messageDto = MessageDto.createMessage(
                appUser.getEmail(),
                "Change Your Root to Rise Password",
                emailHtml,
                MessageMediaTypes.HTML.ordinal()
        );

        emailClient.sendMessage(messageDto);
    }

    private String readHtmlTemplate(String templateName, Map<String, String> templateVariables) throws IOException {
        String emailTemplateDirectory = "src/main/resources/templates/email/";
        String emailHtml = Files.readAllLines(Paths.get(emailTemplateDirectory + templateName)).get(0);

        for (Map.Entry<String, String> templateVariable:templateVariables.entrySet())
            emailHtml = emailHtml.replaceAll(templateVariable.getKey(),templateVariable.getValue());

        return emailHtml;
    }
}