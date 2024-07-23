package com.ProyectoIntegrador.ProyectoIntegrador.auth;

import com.ProyectoIntegrador.ProyectoIntegrador.config.JwtService;
import com.ProyectoIntegrador.ProyectoIntegrador.entity.UserMongoEntity;
import com.ProyectoIntegrador.ProyectoIntegrador.util.Role;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private UserMongoEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserMongoEntity();
        user.setId("1");
        user.setName("User");
        user.setEmail("user@example.com");
        user.setRole(Role.USER);
    }


    @Test
    void getToken() {
        String token = jwtService.getToken(user);
        assertNotNull(token);
    }

    @Test
    void getUsernameFromToken() {
        String token = jwtService.getToken(user);
        String username = jwtService.getUsernameFromToken(token);
        assertEquals("user@example.com", username);
    }

    @Test
    void isTokenValid() {
        String token = jwtService.getToken(user);
        boolean isValid = jwtService.isTokenValid(token, user);
        assertTrue(isValid);
    }

    @Test
    void extractAllClaims() {
        String token = jwtService.getToken(user);
        Claims claims = jwtService.getAllClaims(token);
        assertNotNull(claims);
    }


    @Test
    void isTokenValid_InvalidToken() {
        String invalidToken = "invalid-token";
        boolean isValid = jwtService.isTokenValid(invalidToken, user);
        assertFalse(isValid);
    }

    @Test
    void getUsernameFromToken_InvalidToken() {
        String invalidToken = "invalid-token";
        String username = jwtService.getUsernameFromToken(invalidToken);
        assertNull(username);
    }

}
