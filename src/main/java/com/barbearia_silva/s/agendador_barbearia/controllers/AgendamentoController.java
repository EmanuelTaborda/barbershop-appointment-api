package com.barbearia_silva.s.agendador_barbearia.controllers;


import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoMinDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AgendamentoReponseDTO;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AgendamentoProjection;
import com.barbearia_silva.s.agendador_barbearia.services.AgendamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping(value = "/cliente/{id}")
    public ResponseEntity<List<AgendamentoProjection>> getByCLientId(@PathVariable Long id){
        List<AgendamentoProjection> resultado = agendamentoService.findApointmentsByCLientId(id);
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<AgendamentoReponseDTO> AgendarHorario(@Valid @RequestBody AgendamentoMinDTO agendamentoMinDTO) {
        AgendamentoReponseDTO resultado = agendamentoService.agendarHorario(agendamentoMinDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(resultado);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AgendamentoReponseDTO> atuzalizaragendamento(
            @PathVariable Long id, @Valid @RequestBody AgendamentoMinDTO agendamentoMinDTO){
        AgendamentoReponseDTO resultado = agendamentoService.atualizarAgendamento(id, agendamentoMinDTO);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> cancelarAgendamento(@PathVariable Long id) {
        agendamentoService.excluirAgendamento(id);
        return ResponseEntity.noContent().build();
    }
}
