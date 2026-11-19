package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import br.com.expansaotech.domain.CursoModalidade;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public record CursoInput(
        @NotNull Integer ongId,
        @NotNull Integer professorId,
        @NotBlank @Size(max = 150) String titulo,
        String descricao,
        @NotBlank @Size(max = 100) String horario,
        @NotBlank @Size(max = 255) String localCurso,
        @Size(max = 500) String fotoUrl,
        CursoModalidade modalidade,
        Integer cargaHoraria,
        Integer vagas,
        Integer faixaEtariaMin,
        Integer faixaEtariaMax,
        LocalDate dataInicio,
        LocalDate dataTermino,
        LocalTime horaInicio,
        LocalTime horaFim,
        Set<DayOfWeek> diasSemana,
        Boolean publicado) {

    public CursoInput(
            Integer ongId,
            Integer professorId,
            String titulo,
            String descricao,
            String horario,
            String localCurso,
            String fotoUrl) {
        this(ongId, professorId, titulo, descricao, horario, localCurso, fotoUrl,
                CursoModalidade.INFORMATICA, null, null, null, null, null, null, null, null, Set.of(), true);
    }
}
