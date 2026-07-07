package com.barbershop_appointment_api.controllers;

import com.barbershop_appointment_api.DTOs.BlockDTO;
import com.barbershop_appointment_api.services.BlockService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/bloqueios")
public class BlockController {

    @Autowired
    private BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockDTO> createBlock(@Valid @RequestBody BlockDTO dto) {
        BlockDTO created = blockService.insertBlock(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<BlockDTO> updateBlock(@PathVariable Long id, @Valid @RequestBody BlockDTO blockDTO){
        BlockDTO blockUpdated = blockService.updateBlock(id, blockDTO);
        return ResponseEntity.ok(blockUpdated);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBlock(@PathVariable Long id) {
        blockService.deleteBlock(id);
        return ResponseEntity.noContent().build();
    }
}
