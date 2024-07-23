package com.ProyectoIntegrador.ProyectoIntegrador.auth;

import com.ProyectoIntegrador.ProyectoIntegrador.controller.auth.AuthController;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.AuthDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.LoginDto;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.auth.RegisterDto;
import com.ProyectoIntegrador.ProyectoIntegrador.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        LoginDto loginDto = new LoginDto("user@example.com", "password");
        AuthDto authDto = new AuthDto("token");

        when(authService.login(any(LoginDto.class))).thenReturn(authDto);

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody().getToken());
        verify(authService).login(any(LoginDto.class));
    }




    @Test
    void register() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "password");
        AuthDto authDto = new AuthDto("token");

        when(authService.register(any(RegisterDto.class))).thenReturn(authDto);

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token", response.getBody().getToken());
        verify(authService).register(any(RegisterDto.class));
    }


    @Test
    void login_InvalidCredentials() {
        LoginDto loginDto = new LoginDto("user@example.com", "wrongpassword");
        when(authService.login(any(LoginDto.class))).thenReturn(null);

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertEquals(401, response.getStatusCodeValue()); // Assuming unauthorized for invalid credentials
    }

    @Test
    void register_InvalidData() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "short"); // Suponiendo que "short" es inválido

        when(authService.register(any(RegisterDto.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue()); // Debería retornar 400 Bad Request
        assertNull(response.getBody()); // El cuerpo de la respuesta debería ser null
    }

    @Test
    void login_ServiceException() {
        LoginDto loginDto = new LoginDto("user@example.com", "password");
        when(authService.login(any(LoginDto.class))).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<AuthDto> response = authController.Login(loginDto);

        assertEquals(500, response.getStatusCodeValue()); // Assuming internal server error
    }

    @Test
    void register_ServiceException() {
        RegisterDto registerDto = new RegisterDto("name", "user@example.com", "password");
        when(authService.register(any(RegisterDto.class))).thenThrow(new RuntimeException("Service error"));

        ResponseEntity<AuthDto> response = authController.register(registerDto);

        assertEquals(500, response.getStatusCodeValue()); // Assuming internal server error
    }


}
