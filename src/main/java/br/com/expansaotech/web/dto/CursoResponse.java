package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Curso;
import br.com.expansaotech.domain.CursoModalidade;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public record CursoResponse(
        Integer id,
        Integer ongId,
        String ongNome,
        Integer professorId,
        String professorNome,
        String titulo,
        String descricao,
        String horario,
        String localCurso,
        String fotoUrl,
        LocalDateTime dataCriacao,
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
        boolean publicado) {

    public static CursoResponse from(Curso curso) {
        return new CursoResponse(curso.getId(), curso.getOng().getId(), curso.getOng().getNome(),
                curso.getProfessor().getId(), curso.getProfessor().getUsuario().getNome(),
                curso.getTitulo(), curso.getDescricao(), curso.getHorario(), curso.getLocalCurso(),
                curso.getFotoUrl(), curso.getDataCriacao(), curso.getModalidade(), curso.getCargaHoraria(),
                curso.getVagas(), curso.getFaixaEtariaMin(), curso.getFaixaEtariaMax(), curso.getDataInicio(),
                curso.getDataTermino(), curso.getHoraInicio(), curso.getHoraFim(),
                Set.copyOf(curso.getDiasSemana()), curso.isPublicado());
    }
}
