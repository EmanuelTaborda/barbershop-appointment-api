package com.barbershop_appointment_api.repositories;

import com.barbershop_appointment_api.models.entities.Role;
import com.barbershop_appointment_api.models.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByAuthority(UserType authority);
}
