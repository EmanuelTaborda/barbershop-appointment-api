package com.barbershop_appointment_api.controllers;

import com.barbershop_appointment_api.DTOs.NewUserRequestDTO;
import com.barbershop_appointment_api.DTOs.UserDTO;
import com.barbershop_appointment_api.models.enums.UserType;
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
        NewUserRequestDTO createdUser = userService.createUser(dto, UserType.ROLE_CLIENT);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    @PostMapping(value = "/barbeiro")
    public ResponseEntity<NewUserRequestDTO> insertUserBarber(@Valid @RequestBody NewUserRequestDTO dto){
        NewUserRequestDTO createdUser = userService.createUser(dto, UserType.ROLE_BARBER);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT', 'ROLE_BARBER')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe(){
        UserDTO dto = userService.getMe();
        return ResponseEntity.ok(dto);
    }
}
