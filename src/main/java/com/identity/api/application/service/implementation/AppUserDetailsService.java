package com.identity.api.application.service.implementation;

import com.identity.api.application.domain.model.AppUser;
import com.identity.api.application.domain.model.Role;
import com.identity.api.application.repository.abstraction.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Service("userDetailsService")
public class AppUserDetailsService implements UserDetailsService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Transactional(readOnly=true)
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username);
    }

    private List<GrantedAuthority> buildUserAuthority(Set<Role> userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>();

        for (Role role : userRoles) {
            setAuths.add(new SimpleGrantedAuthority(role.getName()));
        }

        return setAuths.stream().toList();
    }

}