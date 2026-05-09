package com.barbearia_silva.s.agendador_barbearia.controllers;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoDTO;
import com.barbearia_silva.s.agendador_barbearia.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @PostMapping
    public ResponseEntity<AgendamentoDTO> AgendarHorario(@RequestBody AgendamentoDTO dto) {
        dto = agendamentoService.AgendarHorario(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{agendamento}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }
}
