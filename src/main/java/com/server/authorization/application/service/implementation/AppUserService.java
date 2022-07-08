package com.server.authorization.application.service.implementation;

import com.server.authorization.application.domain.model.AppUser;
import com.server.authorization.application.repository.abstraction.AppUserRepository;
import com.server.authorization.web.viewmodel.CreateUserViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userDetailsService")
public class AppUserService implements UserDetailsService {
    private AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        System.out.println(username);
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException(username);
        return user;
    }

    public void createUser(CreateUserViewModel createUserViewModel) {
        this.appUserRepository.saveAndFlush(AppUser.createNewUser(createUserViewModel));
        return;
    }

}