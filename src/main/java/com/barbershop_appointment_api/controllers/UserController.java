package com.barbershop_appointment_api.controllers;

import com.barbershop_appointment_api.DTOs.UserDTO;
import com.barbershop_appointment_api.models.enums.UserType;
import com.barbershop_appointment_api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping(value = "/cliente")
    public ResponseEntity<UserDTO> insertUserClient(@Valid @RequestBody UserDTO dto){
        UserDTO createdUser = userService.createUser(dto, UserType.ROLE_CLIENT);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }

    @Transactional
    @PostMapping(value = "/barbeiro")
    public ResponseEntity<UserDTO> insertUserBarber(@Valid @RequestBody UserDTO dto){
        UserDTO createdUser = userService.createUser(dto, UserType.ROLE_BARBER);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(createdUser);
    }
}
