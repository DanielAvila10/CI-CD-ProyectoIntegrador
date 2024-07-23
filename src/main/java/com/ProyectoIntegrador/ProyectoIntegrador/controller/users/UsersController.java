package com.ProyectoIntegrador.ProyectoIntegrador.controller.users;

import com.ProyectoIntegrador.ProyectoIntegrador.dto.UserDto;
import com.ProyectoIntegrador.ProyectoIntegrador.exception.UserNotFoundException;
import com.ProyectoIntegrador.ProyectoIntegrador.service.UsersService;
import com.ProyectoIntegrador.ProyectoIntegrador.service.UsersServiceMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return usersService.all();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable String id) {
        return usersService.findById(id).orElse(null);
    }

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        return usersService.save(userDto);
    }

    @PutMapping("/{id}")
    public UserDto updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        return usersService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        usersService.deleteById(id);
    }
}