package br.com.expansaotech.web.api;

import br.com.expansaotech.domain.Professor;
import br.com.expansaotech.service.CursoService;
import br.com.expansaotech.service.EventoService;
import br.com.expansaotech.service.OngService;
import br.com.expansaotech.service.UsuarioService;
import br.com.expansaotech.repository.ProfessorRepository;
import br.com.expansaotech.web.dto.CursoInput;
import br.com.expansaotech.web.dto.CursoResponse;
import br.com.expansaotech.web.dto.EventoInput;
import br.com.expansaotech.web.dto.EventoResponse;
import br.com.expansaotech.web.dto.OngInput;
import br.com.expansaotech.web.dto.OngResponse;
import br.com.expansaotech.web.dto.ProfessorResponse;
import br.com.expansaotech.web.dto.UsuarioInput;
import br.com.expansaotech.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api")
public class ApiController {
    private final OngService ongService;
    private final UsuarioService usuarioService;
    private final CursoService cursoService;
    private final EventoService eventoService;
    private final ProfessorRepository professorRepository;

    public ApiController(
            OngService ongService,
            UsuarioService usuarioService,
            CursoService cursoService,
            EventoService eventoService,
            ProfessorRepository professorRepository) {
        this.ongService = ongService;
        this.usuarioService = usuarioService;
        this.cursoService = cursoService;
        this.eventoService = eventoService;
        this.professorRepository = professorRepository;
    }

    @GetMapping("/ongs")
    public List<OngResponse> listarOngs() {
        return ongService.listar().stream().map(OngResponse::from).toList();
    }

    @GetMapping("/ongs/{id}")
    public OngResponse buscarOng(@PathVariable Integer id) {
        return OngResponse.from(ongService.buscar(id));
    }

    @PostMapping("/ongs")
    public ResponseEntity<OngResponse> criarOng(@Valid @RequestBody OngInput input) {
        OngResponse response = OngResponse.from(ongService.salvar(input));
        return created("/api/ongs/" + response.id(), response);
    }

    @PutMapping("/ongs/{id}")
    public OngResponse atualizarOng(@PathVariable Integer id, @Valid @RequestBody OngInput input) {
        return OngResponse.from(ongService.atualizar(id, input));
    }

    @DeleteMapping("/ongs/{id}")
    public ResponseEntity<Void> excluirOng(@PathVariable Integer id) {
        ongService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios")
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listar().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/usuarios/{id}")
    public UsuarioResponse buscarUsuario(@PathVariable Integer id) {
        return UsuarioResponse.from(usuarioService.buscar(id));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody UsuarioInput input) {
        UsuarioResponse response = UsuarioResponse.from(usuarioService.criar(input));
        return created("/api/usuarios/" + response.id(), response);
    }

    @PutMapping("/usuarios/{id}")
    public UsuarioResponse atualizarUsuario(@PathVariable Integer id, @Valid @RequestBody UsuarioInput input) {
        return UsuarioResponse.from(usuarioService.atualizar(id, input));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Integer id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professores")
    public List<ProfessorResponse> listarProfessores() {
        return professorRepository.findAll().stream().map(ProfessorResponse::from).toList();
    }

    @GetMapping("/cursos")
    public List<CursoResponse> listarCursos() {
        return cursoService.listar().stream().map(CursoResponse::from).toList();
    }

    @GetMapping("/cursos/{id}")
    public CursoResponse buscarCurso(@PathVariable Integer id) {
        return CursoResponse.from(cursoService.buscar(id));
    }

    @PostMapping("/cursos")
    public ResponseEntity<CursoResponse> criarCurso(@Valid @RequestBody CursoInput input) {
        CursoResponse response = CursoResponse.from(cursoService.criar(input));
        return created("/api/cursos/" + response.id(), response);
    }

    @PutMapping("/cursos/{id}")
    public CursoResponse atualizarCurso(@PathVariable Integer id, @Valid @RequestBody CursoInput input) {
        return CursoResponse.from(cursoService.atualizar(id, input));
    }

    @DeleteMapping("/cursos/{id}")
    public ResponseEntity<Void> excluirCurso(@PathVariable Integer id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eventos")
    public List<EventoResponse> listarEventos() {
        return eventoService.listar().stream().map(EventoResponse::from).toList();
    }

    @GetMapping("/eventos/{id}")
    public EventoResponse buscarEvento(@PathVariable Integer id) {
        return EventoResponse.from(eventoService.buscar(id));
    }

    @PostMapping("/eventos")
    public ResponseEntity<EventoResponse> criarEvento(@Valid @RequestBody EventoInput input) {
        EventoResponse response = EventoResponse.from(eventoService.criar(input));
        return created("/api/eventos/" + response.id(), response);
    }

    @PutMapping("/eventos/{id}")
    public EventoResponse atualizarEvento(@PathVariable Integer id, @Valid @RequestBody EventoInput input) {
        return EventoResponse.from(eventoService.atualizar(id, input));
    }

    @DeleteMapping("/eventos/{id}")
    public ResponseEntity<Void> excluirEvento(@PathVariable Integer id) {
        eventoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> created(String path, T body) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUri();
        return ResponseEntity.created(location).body(body);
    }
}
