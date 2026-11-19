package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordForm(@NotBlank @Email @Size(max = 100) String email) {}
