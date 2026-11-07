package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Professor;

public record ProfessorResponse(Integer id, String nome, String especialidade) {

    public static ProfessorResponse from(Professor professor) {
        return new ProfessorResponse(professor.getId(), professor.getUsuario().getNome(),
                professor.getEspecialidade());
    }
}
