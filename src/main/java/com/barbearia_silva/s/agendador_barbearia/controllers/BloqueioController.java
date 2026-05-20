package com.barbearia_silva.s.agendador_barbearia.controllers;

import com.barbearia_silva.s.agendador_barbearia.DTOs.BloqueioDTO;
import com.barbearia_silva.s.agendador_barbearia.services.BloqueioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bloqueios")
public class BloqueioController {

    @Autowired
    private BloqueioService bloqueioService;

    @PostMapping
    public ResponseEntity<BloqueioDTO> createBloqueio(@RequestBody BloqueioDTO dto) {
        BloqueioDTO created = bloqueioService.insert(dto);
        return ResponseEntity.ok(created);
    }
}
