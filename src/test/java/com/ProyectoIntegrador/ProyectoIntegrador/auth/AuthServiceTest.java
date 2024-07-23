package com.ProyectoIntegrador.ProyectoIntegrador.auth;

import com.ProyectoIntegrador.ProyectoIntegrador.config.JwtService;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.AuthDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.LoginDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.RegisterDto;
import com.ProyectoIntegrador.ProyectoIntegrador.entity.UserMongoEntity;
import com.ProyectoIntegrador.ProyectoIntegrador.repository.UserMongoRepository;
import com.ProyectoIntegrador.ProyectoIntegrador.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {
    @Mock
    private UserMongoRepository userMongoRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        LoginDto loginDto = new LoginDto("user@example.com", "password");
        UserMongoEntity user = new UserMongoEntity();
        user.setEmail("user@example.com");
        when(userMongoRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        AuthDto authDto = authService.login(loginDto);

        assertNotNull(authDto);
        assertEquals("token", authDto.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userMongoRepository).findByEmail("user@example.com");
        verify(jwtService).getToken(user);
    }

    @Test
    void register() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "password");
        UserMongoEntity user = new UserMongoEntity();
        user.setEmail("user@example.com");
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userMongoRepository.save(any(UserMongoEntity.class))).thenReturn(user);
        when(jwtService.getToken(any(UserDetails.class))).thenReturn("token");

        AuthDto authDto = authService.register(registerDto);

        assertNotNull(authDto);
        assertEquals("token", authDto.getToken());
        verify(passwordEncoder).encode("password");
        verify(userMongoRepository).save(any(UserMongoEntity.class));
        verify(jwtService).getToken(any(UserDetails.class));
    }


    @Test
    void login_UserNotFound() {
        LoginDto loginDto = new LoginDto("user@example.com", "password");
        when(userMongoRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        AuthDto authDto = authService.login(loginDto);

        assertNull(authDto);
    }

    @Test
    void register_UserAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "password");
        when(userMongoRepository.findByEmail("user@example.com")).thenReturn(Optional.of(new UserMongoEntity()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDto));
    }

    @Test
    void register_ErrorDuringSaving() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "password");
        when(userMongoRepository.save(any(UserMongoEntity.class))).thenThrow(new RuntimeException("Saving error"));

        assertThrows(RuntimeException.class, () -> authService.register(registerDto));
    }

    @Test
    void register_EmptyPassword() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "");
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerDto));
    }

}
