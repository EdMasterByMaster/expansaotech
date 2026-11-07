package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Curso;
import java.time.LocalDateTime;

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
        LocalDateTime dataCriacao) {

    public static CursoResponse from(Curso curso) {
        return new CursoResponse(curso.getId(), curso.getOng().getId(), curso.getOng().getNome(),
                curso.getProfessor().getId(), curso.getProfessor().getUsuario().getNome(),
                curso.getTitulo(), curso.getDescricao(), curso.getHorario(), curso.getLocalCurso(),
                curso.getFotoUrl(), curso.getDataCriacao());
    }
}
