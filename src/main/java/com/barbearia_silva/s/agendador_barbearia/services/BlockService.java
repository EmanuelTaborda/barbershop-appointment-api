package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.BlockDTO;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Block;
import com.barbearia_silva.s.agendador_barbearia.repositories.BlockRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BlockDTO insert(BlockDTO dto) {
        Block entity = new Block();
        copyDTOToEntity(dto, entity);
        entity = blockRepository.save(entity);
        return new BlockDTO(entity);
    }

    private void copyDTOToEntity(BlockDTO dto, Block entity) {
        entity.setBarber(userRepository.getReferenceById(dto.getIdBarber()));
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }
}
