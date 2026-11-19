package br.com.expansaotech.domain;

import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EnumType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Curso", indexes = {
    @Index(name = "ix_curso_modalidade", columnList = "modalidade"),
    @Index(name = "ix_curso_publicado", columnList = "publicado")
})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade", nullable = false, length = 20)
    private CursoModalidade modalidade = CursoModalidade.INFORMATICA;

    @Column(name = "carga_horaria")
    private Integer cargaHoraria;

    @Column(name = "vagas")
    private Integer vagas;

    @Column(name = "faixa_etaria_min")
    private Integer faixaEtariaMin;

    @Column(name = "faixa_etaria_max")
    private Integer faixaEtariaMax;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_termino")
    private LocalDate dataTermino;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @ElementCollection
    @CollectionTable(name = "curso_dia_semana", joinColumns = @JoinColumn(name = "id_curso"))
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 12)
    private Set<DayOfWeek> diasSemana = new HashSet<>();

    @Column(nullable = false)
    private boolean publicado = true;

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

    public CursoModalidade getModalidade() {
        return modalidade;
    }

    public void setModalidade(CursoModalidade modalidade) {
        this.modalidade = modalidade == null ? CursoModalidade.INFORMATICA : modalidade;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public Integer getVagas() {
        return vagas;
    }

    public void setVagas(Integer vagas) {
        this.vagas = vagas;
    }

    public Integer getFaixaEtariaMin() {
        return faixaEtariaMin;
    }

    public void setFaixaEtariaMin(Integer faixaEtariaMin) {
        this.faixaEtariaMin = faixaEtariaMin;
    }

    public Integer getFaixaEtariaMax() {
        return faixaEtariaMax;
    }

    public void setFaixaEtariaMax(Integer faixaEtariaMax) {
        this.faixaEtariaMax = faixaEtariaMax;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataTermino() {
        return dataTermino;
    }

    public void setDataTermino(LocalDate dataTermino) {
        this.dataTermino = dataTermino;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Set<DayOfWeek> getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(Set<DayOfWeek> diasSemana) {
        this.diasSemana = diasSemana == null ? new HashSet<>() : new HashSet<>(diasSemana);
    }

    public boolean isPublicado() {
        return publicado;
    }

    public void setPublicado(boolean publicado) {
        this.publicado = publicado;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
