package br.com.expansaotech.service;

import br.com.expansaotech.domain.Professor;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.repository.CursoRepository;
import br.com.expansaotech.repository.ProfessorRepository;
import br.com.expansaotech.repository.UsuarioRepository;
import br.com.expansaotech.web.dto.UsuarioInput;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            ProfessorRepository professorRepository,
            CursoRepository cursoRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("Usuário não encontrado."));
    }

    @Transactional
    public Usuario criar(UsuarioInput input) {
        if (input.senha() == null || input.senha().length() < 12) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 12 caracteres.");
        }
        Usuario usuario = new Usuario();
        aplicar(usuario, input, true);
        Usuario salvo = usuarioRepository.save(usuario);
        sincronizarProfessor(salvo, input.especialidade());
        return salvo;
    }

    @Transactional
    public Usuario atualizar(Integer id, UsuarioInput input) {
        Usuario usuario = buscar(id);
        boolean eraProfessor = professorRepository.existsById(id);
        if (eraProfessor && input.tipo() != UsuarioTipo.PROFESSOR
                && cursoRepository.existsByProfessor_Id(id)) {
            throw new IllegalArgumentException("O usuário está vinculado a cursos e deve permanecer como professor.");
        }
        aplicar(usuario, input, false);
        Usuario salvo = usuarioRepository.save(usuario);
        sincronizarProfessor(salvo, input.especialidade());
        return salvo;
    }

    @Transactional
    public void excluir(Integer id) {
        Usuario usuario = buscar(id);
        if (cursoRepository.existsByProfessor_Id(id)) {
            throw new IllegalArgumentException("Não é possível excluir um professor vinculado a cursos.");
        }
        usuarioRepository.delete(usuario);
    }

    private void aplicar(Usuario usuario, UsuarioInput input, boolean novo) {
        usuario.setCpf(input.cpf());
        usuario.setNome(input.nome());
        usuario.setEmail(input.email());
        usuario.setTipo(input.tipo());
        usuario.setFotoUrl(input.fotoUrl());
        if (input.senha() != null && !input.senha().isBlank()) {
            if (input.senha().length() < 12) {
                throw new IllegalArgumentException("A senha deve ter pelo menos 12 caracteres.");
            }
            usuario.setSenha(passwordEncoder.encode(input.senha()));
        } else if (novo) {
            throw new IllegalArgumentException("A senha é obrigatória para novos usuários.");
        }
        if (input.tipo() == UsuarioTipo.PROFESSOR
                && (input.especialidade() == null || input.especialidade().isBlank())) {
            throw new IllegalArgumentException("Informe a especialidade do professor.");
        }
    }

    private void sincronizarProfessor(Usuario usuario, String especialidade) {
        if (usuario.getTipo() == UsuarioTipo.PROFESSOR) {
            Professor professor = professorRepository.findById(usuario.getId()).orElseGet(Professor::new);
            professor.setUsuario(usuario);
            professor.setEspecialidade(especialidade);
            professorRepository.save(professor);
        } else if (professorRepository.existsById(usuario.getId())) {
            professorRepository.deleteById(usuario.getId());
        }
    }
}
