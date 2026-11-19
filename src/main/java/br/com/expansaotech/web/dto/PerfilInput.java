package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

public record PerfilInput(
        @NotBlank @Pattern(regexp = "[0-9./-]{11,14}", message = "Informe um CPF válido.")
        String cpf,
        @NotBlank @Size(max = 9) @Pattern(regexp = "[0-9]{5}-?[0-9]{3}", message = "Informe um CEP válido.")
        String cep,
        @NotBlank @Size(max = 200) String logradouro,
        @NotBlank @Size(max = 20) String numero,
        @Size(max = 100) String complemento,
        @NotNull @Past LocalDate dataNascimento,
        @NotBlank @Size(max = 20) String telefone,
        @NotEmpty Set<@Size(max = 80) String> areasInteresse,
        Set<DayOfWeek> diasDisponiveis) {
}
