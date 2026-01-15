package com.renato.vanguard_api.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.repository.UserRepository;

@Service
public class UserPropertyService implements UserDetailsService {

    private final UserRepository usuarioRepository;

    public UserPropertyService(UserRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.renato.vanguard_api.model.User user = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found with username: " + username));
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("User")
                .build();
    }
}
