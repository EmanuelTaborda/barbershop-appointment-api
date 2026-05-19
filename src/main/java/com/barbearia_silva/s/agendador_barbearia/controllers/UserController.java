package com.barbearia_silva.s.agendador_barbearia.controllers;

import com.barbearia_silva.s.agendador_barbearia.DTOs.UserDTO;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoUsuario;
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
    @PostMapping(value = "/cliente")
    public ResponseEntity<UserDTO> insertUserClient(@Valid @RequestBody UserDTO dto){
        userService.criarUsuario(dto, TipoUsuario.ROLE_CLIENTE);
        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping(value = "/barbeiro")
    public ResponseEntity<UserDTO> insertUserBarber(@Valid @RequestBody UserDTO dto){
        userService.criarUsuario(dto, TipoUsuario.ROLE_BARBEIRO);
        return ResponseEntity.ok().build();
    }
}
