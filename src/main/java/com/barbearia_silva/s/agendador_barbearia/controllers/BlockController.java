package com.barbearia_silva.s.agendador_barbearia.controllers;

import com.barbearia_silva.s.agendador_barbearia.DTOs.BlockDTO;
import com.barbearia_silva.s.agendador_barbearia.services.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/bloqueios")
public class BlockController {

    @Autowired
    private BlockService blockService;

    @PostMapping
    public ResponseEntity<BlockDTO> createBlock(@RequestBody BlockDTO dto) {
        BlockDTO created = blockService.insert(dto);
        return ResponseEntity.ok(created);
    }
}
