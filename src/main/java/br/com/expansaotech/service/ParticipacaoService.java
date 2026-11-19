package br.com.expansaotech.service;

import br.com.expansaotech.domain.Curso;
import br.com.expansaotech.domain.Evento;
import br.com.expansaotech.domain.InscricaoEvento;
import br.com.expansaotech.domain.MatriculaCurso;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.repository.CursoRepository;
import br.com.expansaotech.repository.EventoRepository;
import br.com.expansaotech.repository.InscricaoEventoRepository;
import br.com.expansaotech.repository.MatriculaCursoRepository;
import br.com.expansaotech.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipacaoService {
    private final CursoRepository cursoRepository;
    private final EventoRepository eventoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaCursoRepository matriculaRepository;
    private final InscricaoEventoRepository inscricaoRepository;

    public ParticipacaoService(
            CursoRepository cursoRepository,
            EventoRepository eventoRepository,
            UsuarioRepository usuarioRepository,
            MatriculaCursoRepository matriculaRepository,
            InscricaoEventoRepository inscricaoRepository) {
        this.cursoRepository = cursoRepository;
        this.eventoRepository = eventoRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaRepository = matriculaRepository;
        this.inscricaoRepository = inscricaoRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void matricular(Integer cursoId, Integer usuarioId) {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Curso não encontrado."));
        Usuario usuario = usuario(usuarioId);
        validarConta(usuario);
        if (!curso.isPublicado()) {
            throw new IllegalArgumentException("Esta turma não está recebendo inscrições.");
        }
        if (curso.getDataTermino() != null && curso.getDataTermino().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O período de inscrição desta turma terminou.");
        }
        if (matriculaRepository.existsByCurso_IdAndUsuario_Id(cursoId, usuarioId)) {
            throw new IllegalArgumentException("Você já está inscrito nesta turma.");
        }
        if (curso.getVagas() != null && matriculaRepository.countByCurso_Id(cursoId) >= curso.getVagas()) {
            throw new IllegalArgumentException("As vagas desta turma foram preenchidas.");
        }
        if (curso.getFaixaEtariaMin() != null || curso.getFaixaEtariaMax() != null) {
            if (usuario.getDataNascimento() == null) {
                throw new IllegalArgumentException("Complete sua data de nascimento no perfil antes de se inscrever.");
            }
            int idade = Period.between(usuario.getDataNascimento(), LocalDate.now()).getYears();
            if ((curso.getFaixaEtariaMin() != null && idade < curso.getFaixaEtariaMin())
                    || (curso.getFaixaEtariaMax() != null && idade > curso.getFaixaEtariaMax())) {
                throw new IllegalArgumentException("Sua idade não está na faixa permitida para esta turma.");
            }
        }
        matriculaRepository.save(new MatriculaCurso(curso, usuario));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void inscreverEvento(Integer eventoId, Integer usuarioId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Evento não encontrado."));
        Usuario usuario = usuario(usuarioId);
        validarConta(usuario);
        if (!evento.isPublicado() || !evento.getDataInicio().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Este evento não está recebendo inscrições.");
        }
        if (evento.getVoluntariosNecessarios() < 1) {
            throw new IllegalArgumentException("Este evento não está com inscrições abertas para voluntários.");
        }
        if (inscricaoRepository.existsByEvento_IdAndUsuario_Id(eventoId, usuarioId)) {
            throw new IllegalArgumentException("Você já está inscrito neste evento.");
        }
        if (inscricaoRepository.countByEvento_Id(eventoId) >= evento.getVoluntariosNecessarios()) {
            throw new IllegalArgumentException("As vagas de voluntariado deste evento foram preenchidas.");
        }
        inscricaoRepository.save(new InscricaoEvento(evento, usuario));
    }

    private Usuario usuario(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado."));
    }

    private void validarConta(Usuario usuario) {
        if (!usuario.isPerfilCompleto()) {
            throw new IllegalArgumentException("Complete seu perfil antes de participar das atividades.");
        }
    }

}
