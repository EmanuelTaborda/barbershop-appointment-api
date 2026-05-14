package com.barbearia_silva.s.agendador_barbearia.repositories;

import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

}
