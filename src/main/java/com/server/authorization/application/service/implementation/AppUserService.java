package com.server.authorization.application.service.implementation;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.domain.model.PasswordResetToken;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.application.repository.abstraction.PasswordTokenRepository;
import com.server.authorization.application.viewmodel.CreateUserViewModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;

@Service("userDetailsService")
public class AppUserService implements UserDetailsService {
    private AppUserRepository appUserRepository;
    private PasswordTokenRepository passwordTokenRepository;

    public AppUserService(AppUserRepository appUserRepository, PasswordTokenRepository passwordTokenRepository) {
        this.appUserRepository = appUserRepository;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsername(username);
        System.out.println("Looking for a user in this context");
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
        return;
    }

    public AppUser findUserByEmail(String email) {
        if(email == null || email.isEmpty()) throw new InvalidParameterException("Email is required");

        AppUser appUser = appUserRepository.findByEmail(email);
        if(appUser == null) throw new InvalidParameterException("User does not exist.");

        return appUser;
    }

    public void createPasswordResetTokenForUser(AppUser appUser, String token) {
        if(appUser == null || token == null || token.isEmpty())
            throw new InvalidParameterException("User and token are required.");
        PasswordResetToken resetToken = PasswordResetToken.generateToken(token, appUser);
        passwordTokenRepository.saveAndFlush(resetToken);
        return;
    }
}