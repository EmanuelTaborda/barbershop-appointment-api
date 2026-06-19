package com.barbearia_silva.s.agendador_barbearia.controllers;


import com.barbearia_silva.s.agendador_barbearia.DTOs.AppointmentMinDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AppointmentReponseDTO;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AppointmentBarberProjection;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AppointmentProjection;
import com.barbearia_silva.s.agendador_barbearia.services.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/agendamento")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @GetMapping(value = "/cliente/{id}")
    public ResponseEntity<List<AppointmentProjection>> getByCLientId(@PathVariable Long id){
        List<AppointmentProjection> result = appointmentService.findApointmentsByCLientId(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<AppointmentReponseDTO> bookAppointment(@Valid @RequestBody AppointmentMinDTO appointmentMinDTO) {
        AppointmentReponseDTO result = appointmentService.bookAppointment(appointmentMinDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<AppointmentReponseDTO> updateAppointment(
            @PathVariable Long id, @Valid @RequestBody AppointmentMinDTO appointmentMinDTO){
        AppointmentReponseDTO result = appointmentService.updateAppointment(id, appointmentMinDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/barbeiro/{id}")
    public ResponseEntity<List<AppointmentBarberProjection>> getAppointmentsByBarberAndDate(
            @PathVariable Long id, @RequestParam(required = true) LocalDate date) {
        List<AppointmentBarberProjection> result = appointmentService.findByBarberAndDate(id, date);
        return ResponseEntity.ok(result);
    }
}
