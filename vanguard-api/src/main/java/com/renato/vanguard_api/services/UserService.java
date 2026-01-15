package com.renato.vanguard_api.services;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.renato.vanguard_api.model.User;
import com.renato.vanguard_api.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository usersRepository) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User RegisterUser(String username, String password) {
        String encryptedPassword = passwordEncoder.encode(password);
        User user = new User(username, encryptedPassword);
        return usersRepository.save(user);
    }

    public Optional<User> buscarPorUsername(String username) {
        return usersRepository.findByUsername(username);
    }
}
