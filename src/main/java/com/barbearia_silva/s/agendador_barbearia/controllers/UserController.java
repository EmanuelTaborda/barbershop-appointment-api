package com.barbearia_silva.s.agendador_barbearia.controllers;

import com.barbearia_silva.s.agendador_barbearia.DTOs.UserDTO;
import com.barbearia_silva.s.agendador_barbearia.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping
    public ResponseEntity<UserDTO> insertUser(@Valid @RequestBody UserDTO dto){
        userService.criateUser(dto);
        return ResponseEntity.ok().build();
    }
}
