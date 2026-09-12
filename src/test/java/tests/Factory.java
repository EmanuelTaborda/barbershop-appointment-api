package tests;

import com.barbershop_appointment_api.models.entities.Role;
import com.barbershop_appointment_api.models.entities.User;

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
}
