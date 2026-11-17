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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
@RequestMapping({"/api/dados", "/api"})
@Tag(name = "Dados da Expansão", description = "API REST para os cadastros da Associação Expansão Cultural")
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

    @GetMapping
    @Operation(summary = "Verificar status da API", description = "Confirma que a API está disponível e identifica o horário da verificação.")
    public ApiStatusResponse getStatus() {
        return new ApiStatusResponse("Rodando perfeitamente!", "Expansão Tech", OffsetDateTime.now(ZoneOffset.UTC));
    }

    @GetMapping("/ongs")
    @Operation(summary = "Listar ONGs", security = @SecurityRequirement(name = "adminBasic"))
    public List<OngResponse> listarOngs() {
        return ongService.listar().stream().map(OngResponse::from).toList();
    }

    @GetMapping("/ongs/{id}")
    @Operation(summary = "Consultar ONG por ID", security = @SecurityRequirement(name = "adminBasic"))
    public OngResponse buscarOng(@PathVariable Integer id) {
        return OngResponse.from(ongService.buscar(id));
    }

    @PostMapping("/ongs")
    @Operation(summary = "Cadastrar ONG", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<OngResponse> criarOng(@Valid @RequestBody OngInput input) {
        OngResponse response = OngResponse.from(ongService.salvar(input));
        return created("/api/dados/ongs/" + response.id(), response);
    }

    @PutMapping("/ongs/{id}")
    @Operation(summary = "Atualizar ONG", security = @SecurityRequirement(name = "adminBasic"))
    public OngResponse atualizarOng(@PathVariable Integer id, @Valid @RequestBody OngInput input) {
        return OngResponse.from(ongService.atualizar(id, input));
    }

    @DeleteMapping("/ongs/{id}")
    @Operation(summary = "Excluir ONG", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<Void> excluirOng(@PathVariable Integer id) {
        ongService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuarios")
    @Operation(summary = "Listar usuários", security = @SecurityRequirement(name = "adminBasic"))
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listar().stream().map(UsuarioResponse::from).toList();
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Consultar usuário por ID", security = @SecurityRequirement(name = "adminBasic"))
    public UsuarioResponse buscarUsuario(@PathVariable Integer id) {
        return UsuarioResponse.from(usuarioService.buscar(id));
    }

    @PostMapping("/usuarios")
    @Operation(summary = "Cadastrar usuário ou professor", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody UsuarioInput input) {
        UsuarioResponse response = UsuarioResponse.from(usuarioService.criar(input));
        return created("/api/dados/usuarios/" + response.id(), response);
    }

    @PutMapping("/usuarios/{id}")
    @Operation(summary = "Atualizar usuário ou professor", security = @SecurityRequirement(name = "adminBasic"))
    public UsuarioResponse atualizarUsuario(@PathVariable Integer id, @Valid @RequestBody UsuarioInput input) {
        return UsuarioResponse.from(usuarioService.atualizar(id, input));
    }

    @DeleteMapping("/usuarios/{id}")
    @Operation(summary = "Excluir usuário", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<Void> excluirUsuario(@PathVariable Integer id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/professores")
    @Operation(summary = "Listar professores", security = @SecurityRequirement(name = "adminBasic"))
    public List<ProfessorResponse> listarProfessores() {
        return professorRepository.findAll().stream().map(ProfessorResponse::from).toList();
    }

    @GetMapping("/cursos")
    @Operation(summary = "Listar cursos publicados")
    public List<CursoResponse> listarCursos() {
        return cursoService.listar().stream().map(CursoResponse::from).toList();
    }

    @GetMapping("/cursos/{id}")
    @Operation(summary = "Consultar curso por ID")
    public CursoResponse buscarCurso(@PathVariable Integer id) {
        return CursoResponse.from(cursoService.buscar(id));
    }

    @PostMapping("/cursos")
    @Operation(summary = "Cadastrar curso", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<CursoResponse> criarCurso(@Valid @RequestBody CursoInput input) {
        CursoResponse response = CursoResponse.from(cursoService.criar(input));
        return created("/api/dados/cursos/" + response.id(), response);
    }

    @PutMapping("/cursos/{id}")
    @Operation(summary = "Atualizar curso", security = @SecurityRequirement(name = "adminBasic"))
    public CursoResponse atualizarCurso(@PathVariable Integer id, @Valid @RequestBody CursoInput input) {
        return CursoResponse.from(cursoService.atualizar(id, input));
    }

    @DeleteMapping("/cursos/{id}")
    @Operation(summary = "Excluir curso", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<Void> excluirCurso(@PathVariable Integer id) {
        cursoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/eventos")
    @Operation(summary = "Listar eventos publicados")
    public List<EventoResponse> listarEventos() {
        return eventoService.listar().stream().map(EventoResponse::from).toList();
    }

    @GetMapping("/eventos/{id}")
    @Operation(summary = "Consultar evento por ID")
    public EventoResponse buscarEvento(@PathVariable Integer id) {
        return EventoResponse.from(eventoService.buscar(id));
    }

    @PostMapping("/eventos")
    @Operation(summary = "Cadastrar evento", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<EventoResponse> criarEvento(@Valid @RequestBody EventoInput input) {
        EventoResponse response = EventoResponse.from(eventoService.criar(input));
        return created("/api/dados/eventos/" + response.id(), response);
    }

    @PutMapping("/eventos/{id}")
    @Operation(summary = "Atualizar evento", security = @SecurityRequirement(name = "adminBasic"))
    public EventoResponse atualizarEvento(@PathVariable Integer id, @Valid @RequestBody EventoInput input) {
        return EventoResponse.from(eventoService.atualizar(id, input));
    }

    @DeleteMapping("/eventos/{id}")
    @Operation(summary = "Excluir evento", security = @SecurityRequirement(name = "adminBasic"))
    public ResponseEntity<Void> excluirEvento(@PathVariable Integer id) {
        eventoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private <T> ResponseEntity<T> created(String path, T body) {
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUri();
        return ResponseEntity.created(location).body(body);
    }
}
