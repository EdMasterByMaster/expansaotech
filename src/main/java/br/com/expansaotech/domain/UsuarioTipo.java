package br.com.expansaotech.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum UsuarioTipo {
    FUNCIONARIO("Funcionario"),
    ADMINISTRACAO("Administracao"),
    COLABORADOR("Colaborador"),
    VOLUNTARIO("Voluntario"),
    PROFESSOR("Professor"),
    DOADOR("Doador"),
    ALUNO("Aluno");

    private final String valorBanco;

    UsuarioTipo(String valorBanco) {
        this.valorBanco = valorBanco;
    }

    @JsonValue
    public String getValorBanco() {
        return valorBanco;
    }

    @JsonCreator
    public static UsuarioTipo fromValorBanco(String valor) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.valorBanco.equals(valor))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo de usuário inválido: " + valor));
    }
}
