package com.ProyectoIntegrador.ProyectoIntegrador.users;


import com.ProyectoIntegrador.ProyectoIntegrador.controller.users.UsersController;
import com.ProyectoIntegrador.ProyectoIntegrador.dto.UserDto;
import com.ProyectoIntegrador.ProyectoIntegrador.exception.UserNotFoundException;
import com.ProyectoIntegrador.ProyectoIntegrador.service.UsersService;
import com.ProyectoIntegrador.ProyectoIntegrador.service.UsersServiceMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsersControllerTest {

    @Mock
    private UsersService usersService;

    @InjectMocks
    private UsersController usersController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers() {
        UserDto user1 = new UserDto();
        user1.setId("1");
        user1.setName("User1");
        user1.setEmail("user1@example.com");

        UserDto user2 = new UserDto();
        user2.setId("2");
        user2.setName("User2");
        user2.setEmail("user2@example.com");

        when(usersService.all()).thenReturn(List.of(user1, user2));

        List<UserDto> users = usersController.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getName());
        verify(usersService).all();
    }

    @Test
    void getUserById() {
        UserDto user = new UserDto();
        user.setId("1");
        user.setName("User");
        user.setEmail("user@example.com");

        when(usersService.findById("1")).thenReturn(Optional.of(user));

        UserDto response = usersController.getUserById("1");

        assertNotNull(response);
        assertEquals("User", response.getName());
        verify(usersService).findById("1");
    }

    @Test
    void getUserById_NotFound() {
        when(usersService.findById("1")).thenReturn(Optional.empty());

        UserDto response = usersController.getUserById("1");

        assertNull(response);
        verify(usersService).findById("1");
    }

    @Test
    void createUser() {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("user@example.com");

        when(usersService.save(any(UserDto.class))).thenReturn(userDto);

        UserDto response = usersController.createUser(userDto);

        assertNotNull(response);
        assertEquals("User", response.getName());
        verify(usersService).save(any(UserDto.class));
    }

    @Test
    void createUser_InvalidData() {
        UserDto userDto = new UserDto();
        userDto.setName("User");

        // Simular el comportamiento del servicio para datos inválidos
        when(usersService.save(any(UserDto.class))).thenThrow(new IllegalArgumentException("Invalid data"));

        assertThrows(IllegalArgumentException.class, () -> usersController.createUser(userDto));
        verify(usersService).save(any(UserDto.class));
    }

    @Test
    void updateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updated@example.com");

        when(usersService.update(any(UserDto.class), eq("1"))).thenReturn(userDto);

        UserDto response = usersController.updateUser("1", userDto);

        assertNotNull(response);
        assertEquals("Updated User", response.getName());
        verify(usersService).update(any(UserDto.class), eq("1"));
    }

    @Test
    void updateUser_NotFound() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updated@example.com");

        when(usersService.update(any(UserDto.class), eq("1"))).thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> usersController.updateUser("1", userDto));
        verify(usersService).update(any(UserDto.class), eq("1"));
    }

    @Test
    void deleteUser() {
        doNothing().when(usersService).deleteById("1");

        usersController.deleteUser("1");

        verify(usersService).deleteById("1");
    }

    @Test
    void deleteUser_NotFound() {
        doThrow(new UserNotFoundException("User not found")).when(usersService).deleteById("1");

        assertThrows(UserNotFoundException.class, () -> usersController.deleteUser("1"));
        verify(usersService).deleteById("1");
    }

}
