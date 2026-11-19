package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record EventoInput(
        @NotNull Integer ongId,
        @NotBlank @Size(max = 150) String titulo,
        String descricao,
        @NotNull LocalDateTime dataHora,
        @NotBlank @Size(max = 255) String localEvento,
        @Size(max = 500) String fotoUrl,
        LocalDateTime dataTermino,
        Integer voluntariosNecessarios,
        Boolean publicado) {

    public EventoInput(
            Integer ongId,
            String titulo,
            String descricao,
            LocalDateTime dataHora,
            String localEvento,
            String fotoUrl) {
        this(ongId, titulo, descricao, dataHora, localEvento, fotoUrl, dataHora, 0, true);
    }
}
