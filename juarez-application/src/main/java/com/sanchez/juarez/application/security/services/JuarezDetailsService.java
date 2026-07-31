package com.sanchez.juarez.application.security.services;

import com.sanchez.juarez.application.security.repositories.UserCredentialsProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary

public class JuarezDetailsService implements UserDetailsService {

    private  final UserCredentialsProvider userCredentialsProvider;

    public JuarezDetailsService(UserCredentialsProvider userCredentialsProvider) {
        this.userCredentialsProvider = userCredentialsProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userCredentialsProvider.findUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
