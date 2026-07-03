package com.barbearia_silva.s.agendador_barbearia.models.projections;

public interface UserDetailsProjection {

    String getUsername();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}

