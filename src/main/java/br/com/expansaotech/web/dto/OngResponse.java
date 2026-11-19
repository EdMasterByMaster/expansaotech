package br.com.expansaotech.web.dto;

import br.com.expansaotech.domain.Ong;
import java.time.LocalDateTime;

public record OngResponse(
        Integer id,
        String cnpj,
        String nome,
        String email,
        String telefone,
        String fotoUrl,
        LocalDateTime dataCadastro,
        String razaoSocial,
        String nomeFantasia,
        String cep,
        String logradouro,
        String numero,
        String complemento) {

    public static OngResponse from(Ong ong) {
        return new OngResponse(ong.getId(), ong.getCnpj(), ong.getNome(), ong.getEmail(),
                ong.getTelefone(), ong.getFotoUrl(), ong.getDataCadastro(), ong.getRazaoSocial(),
                ong.getNomeFantasia(), ong.getCep(), ong.getLogradouro(), ong.getNumero(), ong.getComplemento());
    }
}
