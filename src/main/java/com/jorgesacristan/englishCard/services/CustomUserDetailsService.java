package com.jorgesacristan.englishCard.services;

import com.jorgesacristan.englishCard.models.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService{

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username + " no encontrado"));
    }

    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        return userService.findById(id)
                .orElseThrow(()-> new UsernameNotFoundException("Usuario con ID: " + id + " no encontrado"));

    }

}
