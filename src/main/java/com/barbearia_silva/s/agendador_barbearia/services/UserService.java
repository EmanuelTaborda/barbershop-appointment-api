package com.barbearia_silva.s.agendador_barbearia.services;

import com.barbearia_silva.s.agendador_barbearia.DTOs.UserDTO;
import com.barbearia_silva.s.agendador_barbearia.exceptions.DatabaseException;
import com.barbearia_silva.s.agendador_barbearia.models.entities.Role;
import com.barbearia_silva.s.agendador_barbearia.models.entities.User;
import com.barbearia_silva.s.agendador_barbearia.models.enums.UserType;
import com.barbearia_silva.s.agendador_barbearia.models.projections.UserDetailsProjection;
import com.barbearia_silva.s.agendador_barbearia.repositories.RoleRepository;
import com.barbearia_silva.s.agendador_barbearia.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO createUser(UserDTO dto, UserType userType){
        User entity = new User();

        try {
            copyDTOToEntity(dto, entity);
            Role role = roleRepository.findByAuthority(userType);
            entity.getRoles().add(role);
            userRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Este email já possui um cadastro vinculado");
        }
        return new UserDTO(entity);
    }


    private void copyDTOToEntity(UserDTO dto, User entity){
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setPhone(dto.getPhone());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result){
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }
}
