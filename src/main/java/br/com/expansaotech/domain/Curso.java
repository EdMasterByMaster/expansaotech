package br.com.expansaotech.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Curso")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_curso")
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ong", nullable = false)
    private Ong ong;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_professor", nullable = false)
    private Professor professor;

    @NotBlank
    @Size(max = 150)
    @Nationalized
    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(columnDefinition = "nvarchar(max)")
    private String descricao;

    @NotBlank
    @Size(max = 100)
    @Nationalized
    @Column(nullable = false, length = 100)
    private String horario;

    @NotBlank
    @Size(max = 255)
    @Nationalized
    @Column(name = "local_curso", nullable = false, length = 255)
    private String localCurso;

    @Size(max = 500)
    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public Integer getId() {
        return id;
    }

    public Ong getOng() {
        return ong;
    }

    public void setOng(Ong ong) {
        this.ong = ong;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo == null ? null : titulo.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario == null ? null : horario.trim();
    }

    public String getLocalCurso() {
        return localCurso;
    }

    public void setLocalCurso(String localCurso) {
        this.localCurso = localCurso == null ? null : localCurso.trim();
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl == null ? null : fotoUrl.trim();
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
