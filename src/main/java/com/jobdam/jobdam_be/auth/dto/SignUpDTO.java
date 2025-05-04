package com.jobdam.jobdam_be.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class SignUpDTO {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min = 8, max = 15)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()_\\-+=\\[\\]{};':\"\\\\|,.<>/?]).*$")
    private String password;
}
