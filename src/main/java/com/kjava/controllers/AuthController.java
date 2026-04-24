package com.kjava.controllers;

import com.kjava.config.JwtUtil;
import com.kjava.models.Employe;
import com.kjava.repository.EmployeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String login = body.get("login");
        String motDePasse = body.get("motDePasse");

        Employe employe = employeRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Identifiants incorrects"));

        if (!passwordEncoder.matches(motDePasse, employe.getMotDePasse())) {
            throw new RuntimeException("Identifiants incorrects");
        }

        String token = jwtUtil.generateToken(employe);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", employe.getRole().name(),
                "id", employe.getId(),
                "nom", employe.getNom()
        ));
    }
}