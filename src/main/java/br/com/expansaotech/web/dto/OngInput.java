package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OngInput(
        @NotBlank @Size(max = 18) String cnpj,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 20) String telefone,
        @Size(max = 500) String fotoUrl) {
}
