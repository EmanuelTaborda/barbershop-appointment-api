package com.barbershop_appointment_api.services;

import com.barbershop_appointment_api.exceptions.DatabaseException;
import com.barbershop_appointment_api.exceptions.ForbiddenException;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.models.projections.AppointmentProjection;
import com.barbershop_appointment_api.repositories.AppointmentRepository;
import com.barbershop_appointment_api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
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
    private User client;
    private AppointmentProjection appointmentProjection;

    @Nested
    class FindByClientId {

        @BeforeEach
        void setUp() throws Exception {
            permitedUserId = 1L;
            nonPermitedUsertId = 2L;
            nonExistingClientId = 3L;
            client = Factory.createUserClient();

            appointmentProjection = mock(AppointmentProjection.class);
            List<AppointmentProjection> expectedAppointments = List.of(appointmentProjection);

            when(userRepository.findById(nonExistingClientId)).thenReturn(Optional.empty());
            when(userRepository.findById(permitedUserId)).thenReturn(Optional.of(client));
            when(userRepository.findById(nonPermitedUsertId)).thenReturn(Optional.of(client));

            doNothing().when(validationUserService).validateSelfOrAdminOrBarber(permitedUserId);
            doThrow(new ForbiddenException("Acesso negado")).when(validationUserService).validateSelfOrAdminOrBarber(nonPermitedUsertId);

            when(repository.findByClient(client)).thenReturn(expectedAppointments);
        }


        @Test
        void shouldReturnListOfAppointmentsProjectionsWhenAppointmentExistsAndValidClient() {

            // Act
            List<AppointmentProjection> result = service.findByCLientId(permitedUserId);

            // Assert
            assertNotNull(result, "A lista de agendamentos não deve ser nula");
            assertFalse(result.isEmpty(), "A lista de agendamentos não deve estar vazia");
            assertEquals(appointmentProjection, result.get(0), "O agendamento retornado deve ser o esperado");

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
}
