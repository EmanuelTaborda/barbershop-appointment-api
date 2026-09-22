package com.barbershop_appointment_api.services;

import com.barbershop_appointment_api.DTOs.AppointmentReponseDTO;
import com.barbershop_appointment_api.DTOs.AppointmentRequestDTO;
import com.barbershop_appointment_api.exceptions.AppointmentConflictException;
import com.barbershop_appointment_api.exceptions.DatabaseException;
import com.barbershop_appointment_api.exceptions.ForbiddenException;
import com.barbershop_appointment_api.exceptions.ResourceNotFoundException;
import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.projections.AppointmentProjection;
import com.barbershop_appointment_api.repositories.AppointmentRepository;
import com.barbershop_appointment_api.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataIntegrityViolationException;
import tests.Factory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AppointmentServiceTests {

    @Mock
    private AppointmentRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidationAppointmentService validationAppointmentService;

    @Mock
    private ValidationUserService validationUserService;

    @InjectMocks
    private AppointmentService service;

    private Long nonExistingClientId;
    private Long permitedUserId;
    private Long nonPermitedUsertId;
    private Long existingAppointmentId;
    private Long nonExistingAppointmentId;
    private Long dependentId;
    private User client;
    private User barber;
    private AppointmentProjection appointmentProjection;
    private String validClientEmail;
    private String validBarberEmail;
    private String nonValidEmail;
    private AppointmentRequestDTO requestDto;
    private Appointment appointment;

    @BeforeEach
    void setUpCommon() {
        permitedUserId = 1L;
        nonPermitedUsertId = 2L;
        nonExistingClientId = 3L;
        existingAppointmentId = 4L;
        nonExistingAppointmentId = 5L;
        validClientEmail = "client@gmail.com";
        validBarberEmail = "barber@gmail.com";
        nonValidEmail = "nonValid@gmail.com";
        requestDto = Factory.createAppointmentRequestDTO();
        client = Factory.createUserClient();
        barber = Factory.createUserBarber();
        appointment = Factory.createAppointment();

        doNothing().when(validationUserService).validateSelfOrAdminOrBarber(permitedUserId);
        doThrow(new ForbiddenException("Acesso negado")).when(validationUserService).validateSelfOrAdminOrBarber(nonPermitedUsertId);

        when(userRepository.findByEmail(validClientEmail)).thenReturn(client);
        when(userRepository.findByEmail(validBarberEmail)).thenReturn(barber);
        when(userRepository.findByEmail(nonValidEmail)).thenReturn(null);

        when(repository.save(any(Appointment.class))).
                thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Nested
    class FindByClientId {

        @BeforeEach
        void setUp() throws Exception {

            appointmentProjection = mock(AppointmentProjection.class);
            List<AppointmentProjection> expectedAppointments = List.of(appointmentProjection);

            when(userRepository.findById(nonExistingClientId)).thenReturn(Optional.empty());
            when(userRepository.findById(permitedUserId)).thenReturn(Optional.of(client));
            when(userRepository.findById(nonPermitedUsertId)).thenReturn(Optional.of(client));

            when(repository.findByClient(client)).thenReturn(expectedAppointments);
        }


        @Test
        void shouldReturnListOfAppointmentsProjectionsWhenAppointmentExistsAndValidClient() {

            // Act
            List<AppointmentProjection> result = service.findByCLientId(permitedUserId);

            // Assert
            assertNotNull(result, "A lista de agendamentos não deve ser nula");
            assertFalse(result.isEmpty(), "A lista de agendamentos não deve estar vazia");
            assertEquals(appointmentProjection, result.getFirst(), "O agendamento retornado deve ser o esperado");

            // Verify
            verify(userRepository, times(1)).findById(client.getId());
            verify(validationUserService, times(1)).validateSelfOrAdminOrBarber(client.getId());
            verify(repository, times(1)).findByClient(client);
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenClientDoesNotExist() {

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.findByCLientId(nonExistingClientId)
            );

            assertEquals("Cliente não encontrado: " + nonExistingClientId, ex.getMessage());

            verify(userRepository, times(1)).findById(nonExistingClientId);
            verify(validationUserService, never()).validateSelfOrAdminOrBarber(anyLong());
            verify(repository, never()).findByClient(any());
        }

        @Test
        void shouldThrowDatabaseExceptionWhenClientDoesNotHaveAppointments() {
            when(repository.findByClient(client)).thenReturn(List.of());

            DatabaseException ex = assertThrows(
                    DatabaseException.class,
                    () -> service.findByCLientId(permitedUserId)
            );

            assertEquals("Nenhum agendamento encontrado", ex.getMessage());

            verify(userRepository, times(1)).findById(permitedUserId);
            verify(validationUserService, times(1)).validateSelfOrAdminOrBarber(permitedUserId);
            verify(repository, times(1)).findByClient(client);
        }

        @Test
        void shouldThrowForbiddenExceptionWhenUserDoesNotHavePermission() {
            ForbiddenException ex = assertThrows(
                    ForbiddenException.class,
                    () -> service.findByCLientId(nonPermitedUsertId)
            );

            assertEquals("Acesso negado", ex.getMessage());

            verify(userRepository, times(1)).findById(nonPermitedUsertId);
            verify(validationUserService, times(1)).validateSelfOrAdminOrBarber(nonPermitedUsertId);
            verify(repository, never()).findByClient(any());
        }
    }

    @Nested
    class BookAppointment {

        @Test
        void shouldThrowIllegalArgumentExceptionWhenClientEmailNotFound() {
            requestDto.setClientEmail(nonValidEmail);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.bookAppointment(requestDto)
            );

            assertEquals("Cliente não encontrado: " + nonValidEmail, ex.getMessage());

            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, never()).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenBarberEmailNotFound() {
            requestDto.setClientEmail(validClientEmail);
            requestDto.setBarberEmail(nonValidEmail);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.bookAppointment(requestDto)
            );

            assertEquals("Barbeiro não encontrado: " + nonValidEmail, ex.getMessage());

            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, times(1)).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldNotSaveAppointmentWhenValidationFails() {
            requestDto.setBarberEmail(validBarberEmail);
            requestDto.setClientEmail(validClientEmail);

            doThrow(new AppointmentConflictException("Conflito de agendamento"))
                    .when(validationAppointmentService).validateAppointment(any(Appointment.class));

            AppointmentConflictException ex = assertThrows(
                    AppointmentConflictException.class,
                    () -> service.bookAppointment(requestDto)
            );

            assertEquals("Conflito de agendamento", ex.getMessage());

            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, times(1)).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, times(1)).validateAppointment(any(Appointment.class));
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowForbiddenExceptionWhenUserDoesNotHavePermission() {
            requestDto.setClientEmail(validClientEmail);
            requestDto.setBarberEmail(validBarberEmail);

            doThrow(new ForbiddenException("Acesso negado"))
                    .when(validationUserService).validateSelfOrAdminOrBarber(client.getId());

            ForbiddenException ex = assertThrows(
                    ForbiddenException.class,
                    () -> service.bookAppointment(requestDto)
            );

            assertEquals("Acesso negado", ex.getMessage());

            verify(validationAppointmentService, times(1)).validateAppointment(any(Appointment.class));
            verify(validationUserService, times(1)).validateSelfOrAdminOrBarber(client.getId());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldReturnAppointmentResponseWhenRequestIsValid() {
            requestDto.setBarberEmail(validBarberEmail);
            requestDto.setClientEmail(validClientEmail);

            AppointmentReponseDTO result = service.bookAppointment(requestDto);

            assertNotNull(result, "O DTO não deve ser nulo");

            verify(validationAppointmentService, times(1)).validateAppointment(any(Appointment.class));
            verify(validationUserService, times(1)).validateSelfOrAdminOrBarber(client.getId());
            verify(repository, times(1)).save(any(Appointment.class));
        }
    }

    @Nested
    class UpdateAppointment {

        @BeforeEach
        void setUp() throws Exception {

            when(repository.findById(existingAppointmentId)).thenReturn(Optional.of(appointment));
            when(repository.findById(nonExistingAppointmentId)).thenReturn(Optional.empty());

            doNothing().when(validationUserService).validationForUpdate(existingAppointmentId, requestDto);

        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist(){
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.updateAppointment(nonExistingAppointmentId, requestDto)
            );

            assertEquals("Agendamento não encontrado", ex.getMessage());

            verify(repository, times(1)).findById(nonExistingAppointmentId);
            verify(validationUserService, never()).validationForUpdate(any(), any());
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowForbiddenExceptionWhenUserDoesNotHavePermission() {
            doThrow(new ForbiddenException("Acesso negado"))
                    .when(validationUserService).validationForUpdate(existingAppointmentId, requestDto);

            ForbiddenException ex = assertThrows(
                    ForbiddenException.class,
                    () -> service.updateAppointment(existingAppointmentId, requestDto)
            );

            assertEquals("Acesso negado", ex.getMessage());

            verify(repository, times(1)).findById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForUpdate(existingAppointmentId, requestDto);
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenClientEmailNotFound(){
            requestDto.setClientEmail(nonValidEmail);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.updateAppointment(existingAppointmentId, requestDto)
            );

            assertEquals("Cliente não encontrado: " + nonValidEmail, ex.getMessage());

            verify(repository, times(1)).findById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForUpdate(existingAppointmentId, requestDto);
            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldThrowIllegalArgumentExceptionWhenBarberEmailNotFound(){
            requestDto.setClientEmail(validClientEmail);
            requestDto.setBarberEmail(nonValidEmail);

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.updateAppointment(existingAppointmentId, requestDto)
            );

            assertEquals("Barbeiro não encontrado: " + nonValidEmail, ex.getMessage());

            verify(repository, times(1)).findById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForUpdate(existingAppointmentId, requestDto);
            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, times(1)).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, never()).validateAppointment(any());
            verify(repository, never()).save(any());
        }

        @Test
        void shouldNotUpdateAppointmentWhenValidationFails() {
            requestDto.setBarberEmail(validBarberEmail);
            requestDto.setClientEmail(validClientEmail);

            doThrow(new AppointmentConflictException("Conflito de agendamento"))
                    .when(validationAppointmentService).validateAppointment(any(Appointment.class));

            AppointmentConflictException ex = assertThrows(
                    AppointmentConflictException.class,
                    () -> service.updateAppointment(existingAppointmentId, requestDto)
            );

            assertEquals("Conflito de agendamento", ex.getMessage());

            verify(repository, times(1)).findById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForUpdate(existingAppointmentId, requestDto);
            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, times(1)).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, times(1)).validateAppointment(any(Appointment.class));
            verify(repository, never()).save(any());
        }

        @Test
        void shouldReturnAppointmentResponseWhenRequestIsValid() {
            requestDto.setClientEmail(validClientEmail);
            requestDto.setBarberEmail(validBarberEmail);

            AppointmentReponseDTO result = service.updateAppointment(existingAppointmentId, requestDto);

            assertNotNull(result, "O DTO não deve ser nulo");

            verify(repository, times(1)).findById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForUpdate(existingAppointmentId, requestDto);
            verify(userRepository, times(1)).findByEmail(requestDto.getClientEmail());
            verify(userRepository, times(1)).findByEmail(requestDto.getBarberEmail());
            verify(validationAppointmentService, times(1)).validateAppointment(any(Appointment.class));
            verify(repository, times(1)).save(any(Appointment.class));

        }
    }

    @Nested
    class DeleteAppointment{

        @BeforeEach
        void setUp() throws Exception {

            dependentId = 6L;

            when(repository.existsById(existingAppointmentId)).thenReturn(true);
            when(repository.existsById(dependentId)).thenReturn(true);
            when(repository.existsById(nonExistingAppointmentId)).thenReturn(false);

            doThrow(new DataIntegrityViolationException("Erro simulado de integridade"))
                    .when(repository).deleteById(dependentId);
        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenAppointmentDoesNotExist() {
            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> service.deleteAppointment(nonExistingAppointmentId)
            );

            assertEquals("Agendamento não encontrado: " + nonExistingAppointmentId, ex.getMessage());

            verify(repository, times(1)).existsById(nonExistingAppointmentId);
            verify(validationUserService, never()).validationForDelete(any());
            verify(repository, never()).deleteById(any());
        }

        @Test
        void shouldThrowForbiddenExceptionWhenUserDoesNotHavePermission(){
            doThrow(new ForbiddenException("Acesso negado")).
                    when(validationUserService).validationForDelete(existingAppointmentId);

            ForbiddenException ex = assertThrows(
                    ForbiddenException.class,
                    () -> service.deleteAppointment(existingAppointmentId)
            );

            assertEquals("Acesso negado", ex.getMessage());

            verify(repository, times(1)).existsById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForDelete(existingAppointmentId);
            verify(repository, never()).deleteById(any());
        }

        @Test
        void shouldThrowDatabaseExceptionWhenDataIntegrityViolationOccurs(){
            DatabaseException ex = assertThrows(
                    DatabaseException.class,
                    () -> service.deleteAppointment(dependentId)
            );

            assertEquals("Falha de integridade referencial", ex.getMessage());

            verify(repository, times(1)).existsById(dependentId);
            verify(validationUserService, times(1)).validationForDelete(dependentId);
            verify(repository, times(1)).deleteById(dependentId);
        }

        @Test
        void shouldDeleteAppointmentWhenRequestIsValid(){

            doNothing().when(repository).deleteById(existingAppointmentId);

            Assertions.assertDoesNotThrow(() -> {
                service.deleteAppointment(existingAppointmentId);
            });

            verify(repository, times(1)).existsById(existingAppointmentId);
            verify(validationUserService, times(1)).validationForDelete(existingAppointmentId);
            verify(repository, times(1)).deleteById(existingAppointmentId);
        }
    }
}
