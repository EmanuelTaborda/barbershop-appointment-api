package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.AppointmentRequestDTO;
import com.barbearia_silva.s.agendador_barbearia.DTOs.AppointmentReponseDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.AppointmentConflictException;
import com.barbearia_silva.s.agendador_barbearia.exceptions.DatabaseException;
import com.barbearia_silva.s.agendador_barbearia.exceptions.ResourceNotFoundException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Appointment;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.AppointmentStatus;
import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AppointmentBarberProjection;
import com.barbearia_silva.s.agendador_barbearia.models.projections.AppointmentProjection;
import com.barbearia_silva.s.agendador_barbearia.repositories.AppointmentRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationAppointmentService validator;

    @Transactional(readOnly = true)
    public List<AppointmentProjection> findApointmentsByCLientId(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + id));
        //Recebe cada entity da consulta e transforma em DTO
        List<AppointmentProjection> appointments = appointmentRepository.findByClient(user);
        if (appointments.isEmpty()) {
            throw new DatabaseException("Nenhum agendamento encontrado");
        }

        return appointments;
    }

    @Transactional
    public AppointmentReponseDTO bookAppointment(AppointmentRequestDTO appointmentRequestDTO) {
        Appointment entity = new Appointment();
        copyDTOtoEntity(appointmentRequestDTO, entity);

        validator.validateAppointment(entity);
        entity = appointmentRepository.save(entity);

        return new AppointmentReponseDTO(entity);
    }

    @Transactional
    public AppointmentReponseDTO updateAppointment(Long id, AppointmentRequestDTO appointmentRequestDTO){
            Appointment entity = appointmentRepository.getReferenceById(id);
            if (!entity.getClient().getEmail().equals(appointmentRequestDTO.getClientEmail())
                    && !entity.getBarber().getEmail().equals(appointmentRequestDTO.getBarberEmail())) {
                throw new AppointmentConflictException("Você não tem permissão para realizar este reagendamento.");
            }
            copyDTOtoEntity(appointmentRequestDTO, entity);

            validator.validateAppointment(entity);

            entity = appointmentRepository.save(entity);

            return new AppointmentReponseDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)){
            throw new ResourceNotFoundException("Agendamento não encontrado: " + id);
        }

        try {
            appointmentRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentBarberProjection> findByBarberAndDate(Long Id, LocalDate data) {
        User barbeiro = userRepository.findById(Id).orElseThrow(() -> new ResourceNotFoundException("Barbeiro não encontrado: " + Id));
        List<AppointmentBarberProjection> appointments = appointmentRepository.findByBarberAndDate(barbeiro.getId(), data);
        if (appointments.isEmpty()) {
            throw new DatabaseException("Nenhum agendamento encontrado");
        }
        return appointments;
    }

    private LocalDateTime calculateAppointmentEndTime(LocalDateTime startTime, Set<ServiceType> services) {

        int totalTime = services.stream().mapToInt(ServiceType::getDuracaoMinutos).sum();

        return startTime.plusMinutes(totalTime);
    }

    private void copyDTOtoEntity(AppointmentRequestDTO DTO, Appointment entity) {
        entity.setServices(DTO.getServices());
        entity.setStartTime(DTO.getStartTime());

        //Calculando horário do fim do atendimento para criar a entity
        LocalDateTime endTime = calculateAppointmentEndTime(DTO.getStartTime(), DTO.getServices());
        entity.setEndTime(endTime);

        User cliente = userRepository.findByEmail(DTO.getClientEmail());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado: " + DTO.getClientEmail());
        }

        User barbeiro = userRepository.findByEmail(DTO.getBarberEmail());
        if (barbeiro == null) {
            throw new IllegalArgumentException("Barbeiro não encontrado: " + DTO.getBarberEmail());
        }

        entity.setClient(cliente);
        entity.setBarber(barbeiro);
        entity.setStatus(AppointmentStatus.AGENDADO);
    }
}
