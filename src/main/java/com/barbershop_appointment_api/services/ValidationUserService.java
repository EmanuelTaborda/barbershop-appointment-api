package com.barbershop_appointment_api.services;

import com.barbershop_appointment_api.DTOs.AppointmentRequestDTO;
import com.barbershop_appointment_api.exceptions.ForbiddenException;
import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.enums.UserType;
import com.barbershop_appointment_api.repositories.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ValidationUserService {

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public void validateSelfOrAdmin(Long id) {
        User user = userService.authenticated();
        if (!user.hasRole(UserType.ROLE_ADMIN) && !user.getId().equals(id)){
            throw new ForbiddenException("Acesso negado");
        };
    }

    public void validateSelfOrAdminOrBarber(Long id) {
        User user = userService.authenticated();
        if (!user.hasRole(UserType.ROLE_ADMIN) && !user.hasRole(UserType.ROLE_BARBER) && !user.getId().equals(id)){
            throw new ForbiddenException("Acesso negado");
        };
    }

    public void validationForUpdate(Long idAppointment, AppointmentRequestDTO dto) {
        User user = userService.authenticated();
        Appointment appointment = appointmentRepository.getReferenceById(idAppointment);
        if (!appointment.getClient().getUsername().equals(dto.getClientEmail())) {
            throw new ForbiddenException("O cliente informado não é o mesmo do agendamento inicial");
        } else if (!user.getId().equals(appointment.getBarber().getId()) && !user.getId().equals(appointment.getClient().getId())){
            throw new ForbiddenException("Somente o barbeiro responsável por este atendimento ou " +
                    "o próprio cliente podem alterar o agendamento");
        }
    }

    public void validationForDelete(Long id){
        User user = userService.authenticated();
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (!user.getId().equals(appointment.get().getBarber().getId()) && !user.getId().equals(appointment.get().getClient().getId())){
            throw new ForbiddenException("Somente o barbeiro responsável por este atendimento ou " +
                    "o próprio cliente podem cancelar o agendamento");
        }
    }
}
