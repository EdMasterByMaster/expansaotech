package br.com.expansaotech.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "Usuario")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Size(max = 14)
    @Column(unique = true, length = 14)
    private String cpf;

    @NotBlank
    @Size(max = 150)
    @Nationalized
    @Column(nullable = false, length = 150)
    private String nome;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false, length = 255)
    private String senha;

    @NotNull
    @Convert(converter = UsuarioTipoConverter.class)
    @Column(nullable = false, length = 30)
    private UsuarioTipo tipo;

    @Size(max = 500)
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Size(max = 20)
    @Column(length = 20)
    private String telefone;

    @Size(max = 100)
    @Nationalized
    @Column(length = 100)
    private String cargo;

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

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ElementCollection
    @CollectionTable(name = "usuario_area_interesse", joinColumns = @JoinColumn(name = "id_usuario"))
    @Column(name = "area", nullable = false, length = 80)
    private Set<String> areasInteresse = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "usuario_dia_disponivel", joinColumns = @JoinColumn(name = "id_usuario"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private Set<DayOfWeek> diasDisponiveis = new HashSet<>();

    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = true;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(name = "perfil_completo", nullable = false)
    private boolean perfilCompleto = true;

    @Column(name = "tentativas_login", nullable = false)
    private int tentativasLogin;

    @Column(name = "bloqueado_ate")
    private LocalDateTime bloqueadoAte;

    @CreationTimestamp
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    public Integer getId() {
        return id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf == null ? null : cpf.trim();
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

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @JsonIgnore
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public UsuarioTipo getTipo() {
        return tipo;
    }

    public void setTipo(UsuarioTipo tipo) {
        this.tipo = tipo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl == null ? null : fotoUrl.trim();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone == null ? null : telefone.trim();
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo == null ? null : cargo.trim();
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

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Set<String> getAreasInteresse() {
        return areasInteresse;
    }

    public void setAreasInteresse(Set<String> areasInteresse) {
        this.areasInteresse = areasInteresse == null ? new HashSet<>() : new HashSet<>(areasInteresse);
    }

    public Set<DayOfWeek> getDiasDisponiveis() {
        return diasDisponiveis;
    }

    public void setDiasDisponiveis(Set<DayOfWeek> diasDisponiveis) {
        this.diasDisponiveis = diasDisponiveis == null ? new HashSet<>() : new HashSet<>(diasDisponiveis);
    }

    public boolean isEmailVerificado() {
        return emailVerificado;
    }

    public void setEmailVerificado(boolean emailVerificado) {
        this.emailVerificado = emailVerificado;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isPerfilCompleto() {
        return perfilCompleto;
    }

    public void setPerfilCompleto(boolean perfilCompleto) {
        this.perfilCompleto = perfilCompleto;
    }

    public int getTentativasLogin() {
        return tentativasLogin;
    }

    public void setTentativasLogin(int tentativasLogin) {
        this.tentativasLogin = tentativasLogin;
    }

    public LocalDateTime getBloqueadoAte() {
        return bloqueadoAte;
    }

    public void setBloqueadoAte(LocalDateTime bloqueadoAte) {
        this.bloqueadoAte = bloqueadoAte;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = tipo == UsuarioTipo.ADMINISTRACAO ? "ROLE_ADMIN" : "ROLE_" + tipo.name();
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return bloqueadoAte == null || !bloqueadoAte.isAfter(LocalDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo && emailVerificado;
    }
}
