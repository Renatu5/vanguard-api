package com.renato.vanguard_api.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renato.vanguard_api.model.User;
import com.renato.vanguard_api.security.JwtUtil;
import com.renato.vanguard_api.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService usersService;

    public AuthController(UserService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        User novoUsuario = usersService.RegisterUser(request.get("username"), request.get("senha"));
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User usuario) {
        Optional<User> usuarioExistente = usersService.buscarPorUsername(usuario.getUsername());
        if (usuarioExistente.isPresent() && usuario.getPassword().equals(usuarioExistente.get().getPassword())) {
            String token = JwtUtil.generateToken(usuario.getUsername());
            return ResponseEntity.ok(Map.of("token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}
