package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordForm(
        @NotBlank @Size(min = 12, max = 72) String senha,
        @NotBlank @Size(min = 12, max = 72) String confirmarSenha) {}
