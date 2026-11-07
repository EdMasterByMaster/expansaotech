package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.UsuarioTipo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioInput(
        @NotBlank @Size(max = 14) String cpf,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 255) String senha,
        @NotNull UsuarioTipo tipo,
        @Size(max = 500) String fotoUrl,
        @Size(max = 255) String especialidade) {
}
