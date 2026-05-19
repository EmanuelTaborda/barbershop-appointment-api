package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.UserDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.DatabaseException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Role;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.TipoUsuario;
import com.barbearia_silva.s.agendador_barbearia.repositories.RoleRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserDTO criarUsuario(UserDTO dto, TipoUsuario tipoUsuario){
        User entity = new User();

        try {
            copyDTOToEntity(dto, entity);
            Role role = roleRepository.findByAuthority(tipoUsuario);
            entity.getRoles().add(role);
            userRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Este email já possui um cadastro vinculado");
        }
        return dto;
    }


    private void copyDTOToEntity(UserDTO dto, User entity){
        entity.setNome(dto.getNome());
        entity.setEmail(dto.getEmail());
        entity.setSenha(dto.getSenha());
        entity.setTelefone(dto.getTelefone());
    }
}
