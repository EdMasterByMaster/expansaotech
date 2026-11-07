package br.com.expansaotech.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

class UsuarioTipoTest {
    private final UsuarioTipoConverter converter = new UsuarioTipoConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void converteOsTiposParaOsValoresDoModeloSql() {
        assertThat(converter.convertToDatabaseColumn(UsuarioTipo.ADMINISTRACAO)).isEqualTo("Administracao");
        assertThat(converter.convertToEntityAttribute("Professor")).isEqualTo(UsuarioTipo.PROFESSOR);
    }

    @Test
    void apiUsaOsNomesDeTipoDefinidosNoModelo() throws Exception {
        assertThat(objectMapper.writeValueAsString(UsuarioTipo.FUNCIONARIO)).isEqualTo("\"Funcionario\"");
        assertThat(objectMapper.readValue("\"Aluno\"", UsuarioTipo.class)).isEqualTo(UsuarioTipo.ALUNO);
    }
}
