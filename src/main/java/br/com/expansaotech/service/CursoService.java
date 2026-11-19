package br.com.expansaotech.service;

import br.com.expansaotech.domain.Curso;
import br.com.expansaotech.domain.CursoModalidade;
import br.com.expansaotech.repository.CursoRepository;
import br.com.expansaotech.repository.OngRepository;
import br.com.expansaotech.repository.ProfessorRepository;
import br.com.expansaotech.web.dto.CursoInput;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoService {
    private final CursoRepository cursoRepository;
    private final OngRepository ongRepository;
    private final ProfessorRepository professorRepository;

    public CursoService(
            CursoRepository cursoRepository,
            OngRepository ongRepository,
            ProfessorRepository professorRepository) {
        this.cursoRepository = cursoRepository;
        this.ongRepository = ongRepository;
        this.professorRepository = professorRepository;
    }

    @Transactional(readOnly = true)
    public List<Curso> listar() {
        return cursoRepository.findAllByOrderByTituloAsc();
    }

    @Transactional(readOnly = true)
    public Curso buscar(Integer id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Curso não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<Curso> listarPublicados() {
        return cursoRepository.findAllByPublicadoTrueOrderByTituloAsc();
    }

    @Transactional(readOnly = true)
    public Curso buscarPublicado(Integer id) {
        return cursoRepository.findByIdAndPublicadoTrue(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Curso não encontrado."));
    }

    @Transactional
    public Curso criar(CursoInput input) {
        Curso curso = aplicar(new Curso(), input);
        validarConflitos(curso);
        curso = cursoRepository.save(curso);
        return buscar(curso.getId());
    }

    @Transactional
    public Curso atualizar(Integer id, CursoInput input) {
        Curso curso = aplicar(buscar(id), input);
        validarConflitos(curso);
        curso = cursoRepository.save(curso);
        return buscar(curso.getId());
    }

    @Transactional
    public void excluir(Integer id) {
        cursoRepository.delete(buscar(id));
    }

    private Curso aplicar(Curso curso, CursoInput input) {
        curso.setOng(ongRepository.findById(input.ongId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("ONG não encontrada.")));
        curso.setProfessor(professorRepository.findById(input.professorId())
                .orElseThrow(() -> new RegistroNaoEncontradoException("Professor não encontrado.")));
        curso.setTitulo(input.titulo());
        curso.setDescricao(input.descricao());
        curso.setHorario(input.horario());
        curso.setLocalCurso(input.localCurso());
        curso.setFotoUrl(input.fotoUrl());
        curso.setModalidade(input.modalidade());
        curso.setCargaHoraria(input.cargaHoraria());
        curso.setVagas(input.vagas());
        curso.setFaixaEtariaMin(input.faixaEtariaMin());
        curso.setFaixaEtariaMax(input.faixaEtariaMax());
        curso.setDataInicio(input.dataInicio());
        curso.setDataTermino(input.dataTermino());
        curso.setHoraInicio(input.horaInicio());
        curso.setHoraFim(input.horaFim());
        curso.setDiasSemana(input.diasSemana());
        curso.setPublicado(input.publicado() == null || input.publicado());
        return curso;
    }

    private void validarConflitos(Curso curso) {
        Set<DayOfWeek> dias = curso.getDiasSemana();
        boolean legado = curso.getDataInicio() == null && curso.getHoraInicio() == null
                && curso.getHoraFim() == null && dias.isEmpty();
        if (legado) {
            return;
        }
        if (curso.getVagas() == null || curso.getVagas() < 1) {
            throw new IllegalArgumentException("Informe uma quantidade de vagas maior que zero.");
        }
        if (curso.getCargaHoraria() == null || curso.getCargaHoraria() < 1) {
            throw new IllegalArgumentException("A carga horária deve ser um número maior que zero.");
        }
        if (curso.getDataInicio() == null || curso.getDataTermino() == null
                || curso.getDataTermino().isBefore(curso.getDataInicio())) {
            throw new IllegalArgumentException("Informe um período válido para a turma.");
        }
        LocalTime inicio = curso.getHoraInicio();
        LocalTime fim = curso.getHoraFim();
        if (inicio == null || fim == null || !fim.isAfter(inicio) || dias.isEmpty()) {
            throw new IllegalArgumentException("Informe dias e horários válidos para a turma.");
        }
        if (curso.getModalidade() == CursoModalidade.DANCA
                && (curso.getFaixaEtariaMin() == null || curso.getFaixaEtariaMax() == null
                        || curso.getFaixaEtariaMin() < 0
                        || curso.getFaixaEtariaMax() < curso.getFaixaEtariaMin())) {
            throw new IllegalArgumentException("Informe uma faixa etária válida para a aula de dança.");
        }

        List<Curso> cursos = cursoRepository.findAll();
        for (Curso existente : cursos) {
            if (curso.getId() != null && curso.getId().equals(existente.getId())) {
                continue;
            }
            if (existente.getDataInicio() == null || existente.getHoraInicio() == null
                    || existente.getHoraFim() == null || existente.getDiasSemana().isEmpty()) {
                continue;
            }
            boolean datasSobrepostas = !curso.getDataInicio().isAfter(existente.getDataTermino())
                    && !curso.getDataTermino().isBefore(existente.getDataInicio());
            boolean horariosSobrepostos = inicio.isBefore(existente.getHoraFim())
                    && fim.isAfter(existente.getHoraInicio());
            boolean diasSobrepostos = dias.stream().anyMatch(existente.getDiasSemana()::contains);
            if (datasSobrepostas && horariosSobrepostos && diasSobrepostos
                    && (curso.getProfessor().getId().equals(existente.getProfessor().getId())
                            || curso.getLocalCurso().equalsIgnoreCase(existente.getLocalCurso()))) {
                throw new IllegalArgumentException(
                        "Há conflito de horário para o professor ou para o espaço selecionado.");
            }
        }
    }
}
