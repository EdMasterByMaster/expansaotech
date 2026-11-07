package br.com.expansaotech.service;

import br.com.expansaotech.domain.Curso;
import br.com.expansaotech.repository.CursoRepository;
import br.com.expansaotech.repository.OngRepository;
import br.com.expansaotech.repository.ProfessorRepository;
import br.com.expansaotech.web.dto.CursoInput;
import java.util.List;
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

    @Transactional
    public Curso criar(CursoInput input) {
        Curso curso = cursoRepository.save(aplicar(new Curso(), input));
        return buscar(curso.getId());
    }

    @Transactional
    public Curso atualizar(Integer id, CursoInput input) {
        Curso curso = cursoRepository.save(aplicar(buscar(id), input));
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
        return curso;
    }
}
