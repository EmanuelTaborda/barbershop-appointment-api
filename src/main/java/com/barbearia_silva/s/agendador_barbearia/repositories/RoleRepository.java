package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.models.entities.Role;
import com.barbearia_silva.s.agendador_barbearia.models.enums.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    public Role findByAuthority(UserType authority);
}
