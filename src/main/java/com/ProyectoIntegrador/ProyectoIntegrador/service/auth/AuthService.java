package com.ProyectoIntegrador.ProyectoIntegrador.service.auth;

import com.ProyectoIntegrador.ProyectoIntegrador.config.JwtService;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.AuthDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.LoginDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.RegisterDto;
import com.ProyectoIntegrador.ProyectoIntegrador.entity.UserMongoEntity;
import com.ProyectoIntegrador.ProyectoIntegrador.repository.UserMongoRepository;
import com.ProyectoIntegrador.ProyectoIntegrador.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService {
    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthDto login(LoginDto loginDto) {
        Optional<UserMongoEntity> userOptional = userMongoRepository.findByEmail(loginDto.getUsername());

        if (userOptional.isEmpty()) {
            return null; // User not found
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );
        } catch (Exception e) {
            throw new RuntimeException("Authentication error", e);
        }

        UserMongoEntity user = userOptional.get();
        String token = jwtService.getToken(user);

        return new AuthDto(token);
    }

    public AuthDto register(RegisterDto registerDto) {
        if (registerDto.getPassword().isEmpty() || registerDto.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }

        Optional<UserMongoEntity> existingUser = userMongoRepository.findByEmail(registerDto.getEmail());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        UserMongoEntity newUser = new UserMongoEntity();
        newUser.setName(registerDto.getName());
        newUser.setEmail(registerDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        try {
            userMongoRepository.save(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Error during saving user", e);
        }

        String token = jwtService.getToken(newUser);

        return new AuthDto(token);
    }
}
