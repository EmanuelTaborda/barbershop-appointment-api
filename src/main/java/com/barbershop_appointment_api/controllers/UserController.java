package com.barbershop_appointment_api.controllers;

import com.barbershop_appointment_api.DTOs.NewUserRequestDTO;
import com.barbershop_appointment_api.DTOs.UpdateUserDTO;
import com.barbershop_appointment_api.DTOs.UserDTO;
import com.barbershop_appointment_api.models.enums.UserRole;
import com.barbershop_appointment_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping(value = "/cliente")
    public ResponseEntity<NewUserRequestDTO> insertUserClient(@Valid @RequestBody NewUserRequestDTO dto){
        NewUserRequestDTO createdUser = userService.createUser(dto, UserRole.ROLE_CLIENT);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping(value = "/barbeiro")
    public ResponseEntity<NewUserRequestDTO> insertUserBarber(@Valid @RequestBody NewUserRequestDTO dto){
        NewUserRequestDTO createdUser = userService.createUser(dto, UserRole.ROLE_BARBER);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_BARBER')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe(){
        UserDTO dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        UserDTO updatedUser = userService.updateUser(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
