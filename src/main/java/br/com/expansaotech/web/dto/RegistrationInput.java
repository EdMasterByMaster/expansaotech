package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.UsuarioTipo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationInput(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 12, max = 72) String senha,
        @NotBlank @Size(min = 12, max = 72) String confirmarSenha,
        @NotNull UsuarioTipo tipo) {
}
