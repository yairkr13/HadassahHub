package com.hadassahhub.backend.controller;

import com.hadassahhub.backend.dto.AuthResponseDTO;
import com.hadassahhub.backend.dto.LoginRequestDTO;
import com.hadassahhub.backend.dto.RegisterRequestDTO;
import com.hadassahhub.backend.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            // Let GlobalExceptionHandler handle the exception
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Let GlobalExceptionHandler handle the exception
            throw e;
        }
    }
}