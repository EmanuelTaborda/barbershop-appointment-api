package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.config.BarbershopRules;
import com.barbearia_silva.s.agendador_barbearia.exceptions.AppointmentConflictException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Appointment;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Block;
import com.barbearia_silva.s.agendador_barbearia.models.enums.ServiceType;
import com.barbearia_silva.s.agendador_barbearia.models.enums.UserType;
import com.barbearia_silva.s.agendador_barbearia.repositories.AppointmentRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Service
public class ValidationAppointmentService {

    @Autowired
    AppointmentRepository appointmentRepository;

    @Autowired
    BlockRepository blockRepository;

    public void validateAppointment(Appointment appointment) {
        validateBarber(appointment);
        validateDateAdvance(appointment);
        validateOpeningHours(appointment);
        validateBlockConflicts(appointment);
        validateBarberConflicts(appointment);
    }

    private LocalDateTime calculateEndTIme(LocalDateTime startTime, Set<ServiceType> services) {

        int totalTime = services.stream().mapToInt(ServiceType::getDuracaoMinutos).sum();

        return startTime.plusMinutes(totalTime);
    }

    //Verificar User Barbeiro
    private void validateBarber(Appointment appointment) {
        if (!appointment.getBarber().getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(UserType.ROLE_BARBER))) {
            throw new IllegalArgumentException("O usuário selecionado como barbeiro é inválido.");
        }
    }

    //Verificação se o atendimento solicitado não é no passado e limitando o agendamento para no máximo dois meses de antecedência
    private void validateDateAdvance(Appointment appointment) {
        LocalDateTime agora = LocalDateTime.now();

        if (appointment.getStartTime().isAfter(agora.plusMonths(2))) {
            throw new AppointmentConflictException("É permitido agendar horários com no máximo 2 meses de antecedência.");
        }
    }

    //Função para validar se não há conflitos do horário solicitado no DTO com o horário de funcionamento da barbearia
    private void validateOpeningHours(Appointment appointment) {
        LocalTime startTime = appointment.getStartTime().toLocalTime();
        LocalTime endTime = calculateEndTIme(appointment.getStartTime(), appointment.getServices())
                .toLocalTime();

        //Verificar conflito com dia de domingo
        if (appointment.getStartTime().getDayOfWeek()== DayOfWeek.SUNDAY) {
            throw new AppointmentConflictException("A Barbearia não possui expediente aos domingos.");
        }

        //Verificar conflito com horário de almoço
        Boolean lunchConflict = startTime.isBefore(BarbershopRules.END_LUNCH)
                && endTime.isAfter(BarbershopRules.START_LUNCH);
        if (lunchConflict) {
            throw new AppointmentConflictException("No horário solicitado a Barbearia estará fechada para almoço.");
        }

        //Verificar conflito com horário de funcionamento
        boolean openingHoursConflicts = startTime.isBefore(BarbershopRules.OPENING)
                || startTime.isAfter(BarbershopRules.CLOSING);
        if (openingHoursConflicts) {
            throw new AppointmentConflictException("No horário solicitado a Barbearia estará fechada.");
        }

        //Verifica se o serviço solicitado não termina após fechamento da barbearia
        if (endTime.isAfter(BarbershopRules.CLOSING)) {
            throw new AppointmentConflictException(
                    "Devido ao tempo para realizar seu atendimento, ele precisa iniciar antes, selecione outro horário."
            );
        }
    }

    //Verificar conflito com bloqueios
    private void validateBlockConflicts(Appointment appointment) {
        List<Block> blockConflicts = blockRepository.findBlockConflicts(
                appointment.getBarber(), appointment.getStartTime(),
                appointment.getEndTime());
        if (!blockConflicts.isEmpty()) {
            throw new AppointmentConflictException("O horário solicitado não está disponivél para agendamento.");
        }
    }

    // Verificar disponibilidade do barbeiro
    private void validateBarberConflicts(Appointment appointment) {
        List<Appointment> appointmentConflicts = appointmentRepository.findAppointmentConflicts(
                appointment.getBarber(),
                appointment.getId(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (!appointmentConflicts.isEmpty()) {
            throw new AppointmentConflictException("O barbeiro já possui um agendamento neste horário.");
        }
    }
}
