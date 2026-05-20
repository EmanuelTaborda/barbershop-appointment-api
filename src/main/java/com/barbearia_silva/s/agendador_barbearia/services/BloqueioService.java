package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.BloqueioDTO;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Bloqueio;
import com.barbearia_silva.s.agendador_barbearia.repositories.BloqueioRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BloqueioService {

    @Autowired
    private BloqueioRepository bloqueioRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BloqueioDTO insert(BloqueioDTO dto) {
        Bloqueio entity = new Bloqueio();
        copyDTOToEntity(dto, entity);
        entity = bloqueioRepository.save(entity);
        return new BloqueioDTO(entity);
    }

    private void copyDTOToEntity(BloqueioDTO dto, Bloqueio entity) {
        entity.setBarbeiro(userRepository.getReferenceById(dto.getIdBarbeiro()));
        entity.setInicioBloqueio(dto.getInicioBloqueio());
        entity.setFimBloqueio(dto.getFimBloqueio());
    }
}
