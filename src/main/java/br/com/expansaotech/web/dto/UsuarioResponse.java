package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Integer id,
        String cpf,
        String nome,
        String email,
        UsuarioTipo tipo,
        String fotoUrl,
        LocalDateTime dataCadastro) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getCpf(), usuario.getNome(),
                usuario.getEmail(), usuario.getTipo(), usuario.getFotoUrl(), usuario.getDataCadastro());
    }
}
