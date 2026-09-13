package tests;

import com.barbershop_appointment_api.DTOs.AppointmentReponseDTO;
import com.barbershop_appointment_api.DTOs.AppointmentRequestDTO;
import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.entities.Role;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.enums.ServiceType;

import java.time.LocalDateTime;
import java.util.Set;

public class Factory {

    public static Role createRole() {
        Role role = new Role(1L, "ROLE_CLIENT");
        return role;
    }

    public static User createUserClient() {
        User client = new User();
        client.setId(1L);
        client.setEmail("cliente@gmail.com");
        client.setName("Cliente teste");
        client.setPassword("123456");
        client.setPhone("41999999999");
        client.addRole(new Role(1L, "ROLE_CLIENT"));
        return client;
    }
    
    public static User createUserBarber() {
        User barber = new User();
        barber.setId(2L);
        barber.setEmail("barber@gmail.com");
        barber.setName("Barber teste");
        barber.setPassword("123456");
        barber.setPhone("41999999999");
        barber.addRole(new Role(1L, "ROLE_BARBER"));
        return barber;
    }

    public static AppointmentRequestDTO createAppointmentRequestDTO(){
        AppointmentRequestDTO dto = new AppointmentRequestDTO();
        dto.setStartTime(LocalDateTime.now().plusMinutes(10));
        dto.setBarberEmail("teste@gmail.com");
        dto.setClientEmail("testet@gmail.com");
        dto.setServices(Set.of(ServiceType.CABELO));
        return dto;
    }

    public static Appointment createAppointment() {
        Appointment appointment = new Appointment();
        appointment.setBarber(new User());
        appointment.setClient(new User());
        appointment.setStartTime(LocalDateTime.now().plusMinutes(10));
        appointment.setServices(Set.of(ServiceType.CABELO));
        appointment.setEndTime(appointment.getStartTime().plusMinutes(30));
        return appointment;
    }

    public static AppointmentReponseDTO createAppointmentReponseDTO() {
        AppointmentReponseDTO reponseDTO = new AppointmentReponseDTO();
        return reponseDTO;
    }
}
