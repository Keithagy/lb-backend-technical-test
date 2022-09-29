package com.backendtestka.auth.config;

import com.backendtestka.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    private AuthService customUserDetailsService;

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            final UserDetails userDetail = customUserDetailsService.loadUserByUsername(authentication.getName());

            if (!passwordEncoder().matches(authentication.getCredentials().toString(), userDetail.getPassword())) {
                throw new BadCredentialsException("Wrong password");
            }
            return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), userDetail.getPassword(),
                                                           userDetail.getAuthorities());
        } catch (UsernameNotFoundException e) {
            System.out.println("User Not Found");
            throw e;
        }

    }

}