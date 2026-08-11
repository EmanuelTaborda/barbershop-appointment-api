package com.barbershop_appointment_api.controllers;

import com.barbershop_appointment_api.DTOs.BlockDTO;
import com.barbershop_appointment_api.services.BlockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/bloqueios")
public class BlockController {

    @Autowired
    private BlockService blockService;

    @PreAuthorize("hasRole('ROLE_BARBER')")
    @PostMapping
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody BlockDTO dto) {
        BlockDTO created = blockService.insertBlock(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uri).body(created);
    }

    @PreAuthorize("hasRole('ROLE_BARBER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<BlockDTO> updateBlock(@PathVariable Long id, @Valid @RequestBody BlockDTO blockDTO){
        BlockDTO blockUpdated = blockService.updateBlock(id, blockDTO);
        return ResponseEntity.ok(blockUpdated);
    }

    @PreAuthorize("hasRole('ROLE_BARBER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.noContent().build();
    }
}
