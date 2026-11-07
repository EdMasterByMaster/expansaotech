package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CursoInput(
        @NotNull Integer ongId,
        @NotNull Integer professorId,
        @NotBlank @Size(max = 150) String titulo,
        String descricao,
        @NotBlank @Size(max = 100) String horario,
        @NotBlank @Size(max = 255) String localCurso,
        @Size(max = 500) String fotoUrl) {
}
