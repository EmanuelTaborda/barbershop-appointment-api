package com.barbershop_appointment_api.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserDTO {

    @Pattern(regexp = "^\\s*$|^.{3,100}$",
            message = "O nome deve ter entre 3 e 100 caracteres")
    private String name;

    @Email(message = "O email deve ser válido")
    private String email;

    @Pattern(regexp = "^\\s*$|^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
            message = "O telefone deve estar em um formato válido (ex: (11) 9999-9999)")
    private String phone;
}
