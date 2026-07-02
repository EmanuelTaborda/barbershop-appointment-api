package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AppointmentDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.BlockDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.AppointmentConflictException;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ResourceNotFoundException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Appointment;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Block;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.repositories.AppointmentRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.BlockRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlockService {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional
    public BlockDTO insertBlock(BlockDTO dto) {
        Block entity = new Block();
        blockValidations(dto);
        copyDTOToEntity(dto, entity);
        entity = blockRepository.save(entity);
        return new BlockDTO(entity);
    }

    @Transactional
    public BlockDTO updateBlock(Long id, BlockDTO dto){
        Block entity = blockRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bloqueio não encontrado"));

        blockValidations(dto);

        copyDTOToEntity(dto, entity);
        blockRepository.save(entity);
        return new BlockDTO(entity);
    }

    @Transactional
    public void deleteBlock(Long id) {
        Block entity = blockRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Bloqueio não encontrado"));

        blockRepository.delete(entity);
    }

    private void copyDTOToEntity(BlockDTO dto, Block entity) {
        entity.setBarber(userRepository.getReferenceById(dto.getIdBarber()));
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
    }

    //Verificação de Barbeiro válido e conflito de agendamentos com data de bloqueio
    private void blockValidations(BlockDTO dto) {
        User barber = userRepository.findById(dto.getIdBarber())
                .orElseThrow(() -> new ResourceNotFoundException("Barbeiro Não encontrado"));

        List<Appointment> conflictAppointments = appointmentRepository.findAppointmentConflictsforBlocks
                (barber, dto.getStartTime(), dto.getEndTime());

        if (!conflictAppointments.isEmpty()){
            throw new AppointmentConflictException("Existem atendimentos marcados durante este período");
        }
    }
}
