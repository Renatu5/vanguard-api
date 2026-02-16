package com.renato.vanguard_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.vanguard_api.config.security.TokenConfig;
import com.renato.vanguard_api.dto.request.LoginRequest;
import com.renato.vanguard_api.dto.request.RegisterUserRequest;
import com.renato.vanguard_api.dto.response.LoginResponse;
import com.renato.vanguard_api.dto.response.RegisterUserResponse;
import com.renato.vanguard_api.model.User;
import com.renato.vanguard_api.repository.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userPass = new UsernamePasswordAuthenticationToken(request.email(),
                request.password());
        Authentication authentication = authenticationManager.authenticate(userPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUsername(request.name());
        user.setEmail(request.email());

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterUserResponse(user.getUsername(), user.getEmail()));

    }
}
