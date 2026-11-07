package br.com.expansaotech.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Ong")
public class Ong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ong")
    private Integer id;

    @NotBlank
    @Size(max = 18)
    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @NotBlank
    @Size(max = 150)
    @Nationalized
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Size(max = 500)
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Integer getId() {
        return id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj == null ? null : cnpj.trim();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome == null ? null : nome.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone == null ? null : telefone.trim();
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl == null ? null : fotoUrl.trim();
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }
}
