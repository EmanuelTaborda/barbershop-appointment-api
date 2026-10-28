package com.barbershop_appointment_api.services;

import com.barbershop_appointment_api.DTOs.BlockDTO;
import com.barbershop_appointment_api.exceptions.AppointmentConflictException;
import com.barbershop_appointment_api.exceptions.ResourceNotFoundException;
import com.barbershop_appointment_api.models.entities.Appointment;
import com.barbershop_appointment_api.models.entities.Block;
import com.barbershop_appointment_api.models.entities.User;
import com.barbershop_appointment_api.repositories.AppointmentRepository;
import com.barbershop_appointment_api.repositories.BlockRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BlockServiceTests {

    @InjectMocks
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private BlockDTO dto;
    private Block block;
    private Long existingId;
    private Long nonExistingId;
    private User barber;
    private Appointment appointment;

    @BeforeEach
    void setUpCommon() {
        dto = Factory.createBlockDTO();
        block = Factory.createBlock();
        existingId = 1L;
        nonExistingId = 2L;
        barber = Factory.createUserBarber();
        appointment = Factory.createAppointment();

        when(userRepository.findById(existingId)).thenReturn(Optional.of(barber));
        when(userRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(blockRepository.findById(existingId)).thenReturn(Optional.of(block));
        when(blockRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(userRepository.getReferenceById(existingId)).thenReturn(barber);

    }


    @Nested
    class InsertBlock{

        @Test
        void shouldThrowResourceNotFoundExceptionWhenBarberDoesNotExist() {
            dto.setIdBarber(nonExistingId);

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> blockService.insertBlock(dto)
            );

            assertEquals("Barbeiro não encontrado", ex.getMessage());

            verifyNoInteractions(appointmentRepository, blockRepository);
        }

        @Test
        void shouldThrowAppointmentConflictExceptionWhenBarberHasAppointmentsInBlockedPeriod() {
            dto.setIdBarber(existingId);

            when(appointmentRepository.findAppointmentConflictsforBlocks(barber, dto.getStartTime(), dto.getEndTime())).
                    thenReturn(List.of(appointment));

            AppointmentConflictException ex = assertThrows(
                    AppointmentConflictException.class,
                    () -> blockService.insertBlock(dto)
            );

            assertEquals("Existem atendimentos marcados durante este período", ex.getMessage());

            verifyNoInteractions(blockRepository);
        }

        @Test
        void shouldSaveBlockAndReturnBlockDTOWhenRequestIsValid() {
            dto.setIdBarber(existingId);

            when(userRepository.getReferenceById(existingId)).thenReturn(barber);

            when(appointmentRepository.findAppointmentConflictsforBlocks(barber, dto.getStartTime(), dto.getEndTime())).
                    thenReturn(List.of());

            when(blockRepository.save(any(Block.class))).
                    thenAnswer(invocation -> invocation.getArgument(0));

            BlockDTO result = blockService.insertBlock(dto);

            assertNotNull(result, "O DTO não deve ser nulo");
            assertEquals(dto.getStartTime(), result.getStartTime());
            assertEquals(dto.getEndTime(), result.getEndTime());

        }
    }

    @Nested
    class UpdateBlock{

        @Test
        void shouldThrowResourceNotFoundExceptionWhenBlockDoesNotExist(){

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> blockService.updateBlock(nonExistingId, dto)
            );

            assertEquals("Bloqueio não encontrado", ex.getMessage());

            verifyNoInteractions(userRepository, appointmentRepository);

        }

        @Test
        void shouldThrowResourceNotFoundExceptionWhenBarberDoesNotExist() {
            dto.setIdBarber(nonExistingId);

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> blockService.updateBlock(existingId, dto)
            );

            assertEquals("Barbeiro não encontrado", ex.getMessage());

            verify(blockRepository, never()).save(any());
        }

        @Test
        void shouldThrowAppointmentConflictExceptionWhenBarberHasAppointmentsInBlockedPeriod () {
            dto.setIdBarber(existingId);
            when(appointmentRepository.findAppointmentConflictsforBlocks(barber, dto.getStartTime(), dto.getEndTime()))
                    .thenReturn(List.of(appointment));

            AppointmentConflictException ex = assertThrows(
                    AppointmentConflictException.class,
                    () -> blockService.updateBlock(existingId, dto)
            );

            assertEquals("Existem atendimentos marcados durante este período", ex.getMessage());

            verify(blockRepository, never()).save(any());
        }

        @Test
        void shouldUpdateBlockAndReturnBlockDTOWhenRequestIsValid () {
            dto.setIdBarber(existingId);

            when(appointmentRepository.findAppointmentConflictsforBlocks(barber, dto.getStartTime(), dto.getEndTime()))
                    .thenReturn(List.of());
            when(blockRepository.save(any(Block.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            BlockDTO result = blockService.updateBlock(existingId, dto);

            assertNotNull(result, "O DTO não deve ser nulo");
            assertEquals(dto.getStartTime(), result.getStartTime());
            assertEquals(dto.getEndTime(), result.getEndTime());

            verify(blockRepository).save(any(Block.class));
        }
    }

    @Nested
    class DeleteBlock{

        @Test
        void shouldThrowResourceNotFoundExceptionWhenBlockDoesNotExist() {

            ResourceNotFoundException ex = assertThrows(
                    ResourceNotFoundException.class,
                    () -> blockService.deleteBlock(nonExistingId)
            );

            assertEquals("Bloqueio não encontrado", ex.getMessage());

            verify(blockRepository, never()).delete(any());
        }

        @Test
        void shouldDeleteBlockWhenRequestIsValid() {

            blockService.deleteBlock(existingId);

            verify(blockRepository).delete(block);
        }
    }
}
