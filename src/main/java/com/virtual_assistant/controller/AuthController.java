package com.virtual_assistant.controller;

import com.virtual_assistant.dto.AuthResponse;
import com.virtual_assistant.dto.LoginRequest;
import com.virtual_assistant.dto.UserDTO;
import com.virtual_assistant.model.User;
import com.virtual_assistant.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody User user, HttpServletResponse response) {
        AuthResponse authResponse = authService.signup(user);
        String token = authResponse.getToken();
        addJwtCookie(response, token);

        UserDTO userDTO = authResponse.getUser();

        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request.getEmail(), request.getPassword());
        String token = authResponse.getToken();
        addJwtCookie(response, token);

        UserDTO userDTO = authResponse.getUser();

        return ResponseEntity.ok(userDTO);
    }



    @DeleteMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("jwt", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }



    private void addJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);       // JS can't read it
        cookie.setSecure(false);        // true in production (HTTPS only)
        cookie.setPath("/");            // valid for all endpoints
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(cookie);
    }
}
