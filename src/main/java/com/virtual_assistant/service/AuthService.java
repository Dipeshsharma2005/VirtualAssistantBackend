package com.virtual_assistant.service;

import com.virtual_assistant.dto.AuthResponse;
import com.virtual_assistant.dto.UserDTO;
import com.virtual_assistant.model.User;
import com.virtual_assistant.repository.UserRepository;
import com.virtual_assistant.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse signup(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("Email already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        UserDTO userDTO = new UserDTO(savedUser.getId(),savedUser.getName(),savedUser.getEmail(),savedUser.getAssistantName(),savedUser.getAssistantImage(),savedUser.getHistory());

        return new AuthResponse(token, userDTO);
    }

    public AuthResponse login(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        String token = jwtUtil.generateToken(email);
        UserDTO userDTO = new UserDTO(user.getId(),user.getName(),user.getEmail(),user.getAssistantName(),user.getAssistantImage(),user.getHistory());
        return new AuthResponse(token, userDTO);
    }

    public Optional<UserDTO> updateAssistant(Long id, String assistantName, String imageUrl, MultipartFile file) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return Optional.empty();
        }

        User user = optionalUser.get();

        // Update assistant name if provided
        if (assistantName != null && !assistantName.isBlank()) {
            user.setAssistantName(assistantName);
        }

        // Handle image: upload new file OR take given imageUrl
        String assistantImage = null;
        try {
            if (file != null && !file.isEmpty()) {
                assistantImage = cloudinaryService.uploadFile(file);
            } else if (imageUrl != null && !imageUrl.isBlank()) {
                assistantImage = imageUrl;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing image: " + e.getMessage());
        }

        if (assistantImage != null) {
            user.setAssistantImage(assistantImage);
        }

        userRepository.save(user);

        UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getAssistantName(), user.getAssistantImage(),user.getHistory());

        return Optional.of(userDTO);
    }
}
