package com.virtual_assistant.controller;

import com.virtual_assistant.dto.AuthResponse;
import com.virtual_assistant.dto.LoginRequest;
import com.virtual_assistant.dto.UserDTO;
import com.virtual_assistant.model.User;
import com.virtual_assistant.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody User user, HttpServletResponse response) {
        AuthResponse authResponse = authService.signup(user);
        addJwtCookie(response, authResponse.getToken());
        return new ResponseEntity<>(authResponse.getUser(), HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());
        addJwtCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse.getUser());
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletResponse response) {
        // Expire the cookie
        response.addHeader("Set-Cookie", "jwt=; HttpOnly; Path=/; Max-Age=0; SameSite=None; Secure");
    }

    // Add JWT cookie via Set-Cookie header for cross-origin SPA
    private void addJwtCookie(HttpServletResponse response, String token) {
        String cookie = "jwt=" + token + "; HttpOnly; Path=/; Max-Age=86400; SameSite=None; Secure";
        response.addHeader("Set-Cookie", cookie);
    }
}
