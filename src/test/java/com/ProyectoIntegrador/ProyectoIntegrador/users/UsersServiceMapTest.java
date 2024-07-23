package com.ProyectoIntegrador.ProyectoIntegrador.users;

import com.ProyectoIntegrador.ProyectoIntegrador.dto.UserDto;
import com.ProyectoIntegrador.ProyectoIntegrador.entity.UserMongoEntity;
import com.ProyectoIntegrador.ProyectoIntegrador.repository.UserMongoRepository;
import com.ProyectoIntegrador.ProyectoIntegrador.service.UsersServiceMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsersServiceMapTest {

    @Mock
    private UserMongoRepository userMongoRepository;

    @InjectMocks
    private UsersServiceMap usersServiceMap;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void all() {
        UserMongoEntity user1 = new UserMongoEntity();
        user1.setId("1");
        user1.setName("User1");
        user1.setEmail("user1@example.com");

        UserMongoEntity user2 = new UserMongoEntity();
        user2.setId("2");
        user2.setName("User2");
        user2.setEmail("user2@example.com");

        when(userMongoRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> users = usersServiceMap.all();

        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User2", users.get(1).getName());
        verify(userMongoRepository).findAll();
    }

    @Test
    void findById() {
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("User");
        user.setEmail("user@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));

        Optional<UserDto> foundUser = usersServiceMap.findById("1");

        assertTrue(foundUser.isPresent());
        assertEquals("User", foundUser.get().getName());
        verify(userMongoRepository).findById("1");
    }

    @Test
    void save() {
        UserDto userDto = new UserDto();
        userDto.setName("User");
        userDto.setEmail("user@example.com");

        UserMongoEntity user = new UserMongoEntity();
        user.setName("User");
        user.setEmail("user@example.com");

        when(userMongoRepository.save(any(UserMongoEntity.class))).thenReturn(user);

        UserDto savedUser = usersServiceMap.save(userDto);

        assertNotNull(savedUser);
        assertEquals("User", savedUser.getName());
        verify(userMongoRepository).save(any(UserMongoEntity.class));
    }

    @Test
    void update() {
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updated@example.com");

        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");
        user.setName("User");
        user.setEmail("user@example.com");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));
        when(userMongoRepository.save(any(UserMongoEntity.class))).thenReturn(user);

        UserDto updatedUser = usersServiceMap.update(userDto, "1");

        assertNotNull(updatedUser);
        assertEquals("Updated User", updatedUser.getName());
        verify(userMongoRepository).findById("1");
        verify(userMongoRepository).save(user);
    }

    @Test
    void deleteById() {
        UserMongoEntity user = new UserMongoEntity();
        user.setId("1");

        when(userMongoRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userMongoRepository).delete(user);

        usersServiceMap.deleteById("1");

        verify(userMongoRepository).findById("1");
        verify(userMongoRepository).delete(user);
    }
}
