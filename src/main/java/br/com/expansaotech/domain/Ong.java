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

    @Size(max = 200)
    @Nationalized
    @Column(name = "razao_social", length = 200)
    private String razaoSocial;

    @Size(max = 150)
    @Nationalized
    @Column(name = "nome_fantasia", length = 150)
    private String nomeFantasia;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String email;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Size(max = 9)
    @Column(length = 9)
    private String cep;

    @Size(max = 200)
    @Nationalized
    @Column(length = 200)
    private String logradouro;

    @Size(max = 20)
    @Column(length = 20)
    private String numero;

    @Size(max = 100)
    @Nationalized
    @Column(length = 100)
    private String complemento;

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

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial == null ? null : razaoSocial.trim();
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia == null ? null : nomeFantasia.trim();
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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep == null ? null : cep.trim();
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro == null ? null : logradouro.trim();
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero == null ? null : numero.trim();
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? null : complemento.trim();
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
