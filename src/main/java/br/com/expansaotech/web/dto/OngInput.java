package br.com.expansaotech.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OngInput(
        @NotBlank @Size(max = 18) String cnpj,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Email @Size(max = 100) String email,
        @Size(max = 20) String telefone,
        @Size(max = 500) String fotoUrl,
        @Size(max = 200) String razaoSocial,
        @Size(max = 150) String nomeFantasia,
        @Size(max = 9) String cep,
        @Size(max = 200) String logradouro,
        @Size(max = 20) String numero,
        @Size(max = 100) String complemento) {

    public OngInput(String cnpj, String nome, String email, String telefone, String fotoUrl) {
        this(cnpj, nome, email, telefone, fotoUrl, null, null, null, null, null, null);
    }
}
