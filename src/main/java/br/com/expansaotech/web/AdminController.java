package br.com.expansaotech.web;

import br.com.expansaotech.domain.Curso;
import br.com.expansaotech.domain.Evento;
import br.com.expansaotech.domain.Ong;
import br.com.expansaotech.domain.Professor;
import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.repository.ProfessorRepository;
import br.com.expansaotech.service.CursoService;
import br.com.expansaotech.service.EventoService;
import br.com.expansaotech.service.OngService;
import br.com.expansaotech.service.UsuarioService;
import br.com.expansaotech.web.dto.CursoInput;
import br.com.expansaotech.web.dto.EventoInput;
import br.com.expansaotech.web.dto.OngInput;
import br.com.expansaotech.web.dto.UsuarioInput;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final OngService ongService;
    private final UsuarioService usuarioService;
    private final CursoService cursoService;
    private final EventoService eventoService;
    private final ProfessorRepository professorRepository;

    public AdminController(
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
    public String dashboard(Model model) {
        model.addAttribute("totalOngs", ongService.listar().size());
        model.addAttribute("totalUsuarios", usuarioService.listar().size());
        model.addAttribute("totalCursos", cursoService.listar().size());
        model.addAttribute("totalEventos", eventoService.listar().size());
        return "admin/dashboard";
    }

    @GetMapping("/ongs")
    public String ongs(Model model) {
        model.addAttribute("ongs", ongService.listar());
        return "admin/ongs";
    }

    @GetMapping("/ongs/nova")
    public String novaOng(Model model) {
        model.addAttribute("form", new OngInput("", "", "", "", ""));
        model.addAttribute("titulo", "Cadastrar ONG");
        return "admin/ong-form";
    }

    @GetMapping("/ongs/{id}/editar")
    public String editarOng(@PathVariable Integer id, Model model) {
        Ong ong = ongService.buscar(id);
        model.addAttribute("form", new OngInput(
                ong.getCnpj(), ong.getNome(), ong.getEmail(), ong.getTelefone(), ong.getFotoUrl()));
        model.addAttribute("id", id);
        model.addAttribute("titulo", "Editar ONG");
        return "admin/ong-form";
    }

    @PostMapping("/ongs")
    public String criarOng(
            @Valid @ModelAttribute("form") OngInput form,
            BindingResult result,
                Model model,
                RedirectAttributes redirect) {
            if (result.hasErrors()) {
                model.addAttribute("titulo", "Cadastrar ONG");
                return "admin/ong-form";
        }
        ongService.salvar(form);
        redirect.addFlashAttribute("sucesso", "ONG cadastrada.");
        return "redirect:/admin/ongs";
    }

    @PostMapping("/ongs/{id}")
    public String atualizarOng(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") OngInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar ONG");
            model.addAttribute("id", id);
            model.addAttribute("titulo", "Editar ONG");
            return "admin/ong-form";
        }
        ongService.atualizar(id, form);
        redirect.addFlashAttribute("sucesso", "ONG atualizada.");
        return "redirect:/admin/ongs";
    }

    @PostMapping("/ongs/{id}/excluir")
    public String excluirOng(@PathVariable Integer id, RedirectAttributes redirect) {
        ongService.excluir(id);
        redirect.addFlashAttribute("sucesso", "ONG excluída.");
        return "redirect:/admin/ongs";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/novo")
    public String novoUsuario(Model model) {
        prepararFormularioUsuario(model, new UsuarioInput("", "", "", "", UsuarioTipo.ALUNO, "", ""));
        return "admin/usuario-form";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioService.buscar(id);
        String especialidade = professorRepository.findById(id).map(Professor::getEspecialidade).orElse("");
        prepararFormularioUsuario(model, new UsuarioInput(
                usuario.getCpf(), usuario.getNome(), usuario.getEmail(), "", usuario.getTipo(),
                usuario.getFotoUrl(), especialidade));
        model.addAttribute("id", id);
        return "admin/usuario-form";
    }

    @PostMapping("/usuarios")
    public String criarUsuario(
            @Valid @ModelAttribute("form") UsuarioInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (form.senha() == null || form.senha().length() < 12) {
            result.rejectValue("senha", "senha.invalida", "A senha deve ter pelo menos 12 caracteres.");
        }
        if (form.tipo() == UsuarioTipo.PROFESSOR && (form.especialidade() == null || form.especialidade().isBlank())) {
            result.rejectValue("especialidade", "especialidade.obrigatoria", "Informe a especialidade.");
        }
        if (result.hasErrors()) {
            model.addAttribute("tipos", UsuarioTipo.values());
            return "admin/usuario-form";
        }
        usuarioService.criar(form);
        redirect.addFlashAttribute("sucesso", "Usuário cadastrado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}")
    public String atualizarUsuario(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") UsuarioInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (form.senha() != null && !form.senha().isBlank() && form.senha().length() < 12) {
            result.rejectValue("senha", "senha.invalida", "A senha deve ter pelo menos 12 caracteres.");
        }
        if (form.tipo() == UsuarioTipo.PROFESSOR && (form.especialidade() == null || form.especialidade().isBlank())) {
            result.rejectValue("especialidade", "especialidade.obrigatoria", "Informe a especialidade.");
        }
        if (result.hasErrors()) {
            model.addAttribute("tipos", UsuarioTipo.values());
            model.addAttribute("id", id);
            return "admin/usuario-form";
        }
        usuarioService.atualizar(id, form);
        redirect.addFlashAttribute("sucesso", "Usuário atualizado.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/excluir")
    public String excluirUsuario(@PathVariable Integer id, RedirectAttributes redirect) {
        usuarioService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Usuário excluído.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/cursos")
    public String cursos(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        return "admin/cursos";
    }

    @GetMapping("/cursos/novo")
    public String novoCurso(Model model) {
        prepararFormularioCurso(model, new CursoInput(null, null, "", "", "", "", ""));
        return "admin/curso-form";
    }

    @GetMapping("/cursos/{id}/editar")
    public String editarCurso(@PathVariable Integer id, Model model) {
        Curso curso = cursoService.buscar(id);
        prepararFormularioCurso(model, new CursoInput(curso.getOng().getId(), curso.getProfessor().getId(),
                curso.getTitulo(), curso.getDescricao(), curso.getHorario(), curso.getLocalCurso(), curso.getFotoUrl()));
        model.addAttribute("id", id);
        return "admin/curso-form";
    }

    @PostMapping("/cursos")
    public String criarCurso(
            @Valid @ModelAttribute("form") CursoInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormularioCurso(model, form);
            return "admin/curso-form";
        }
        cursoService.criar(form);
        redirect.addFlashAttribute("sucesso", "Curso cadastrado.");
        return "redirect:/admin/cursos";
    }

    @PostMapping("/cursos/{id}")
    public String atualizarCurso(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") CursoInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormularioCurso(model, form);
            model.addAttribute("id", id);
            return "admin/curso-form";
        }
        cursoService.atualizar(id, form);
        redirect.addFlashAttribute("sucesso", "Curso atualizado.");
        return "redirect:/admin/cursos";
    }

    @PostMapping("/cursos/{id}/excluir")
    public String excluirCurso(@PathVariable Integer id, RedirectAttributes redirect) {
        cursoService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Curso excluído.");
        return "redirect:/admin/cursos";
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoService.listar());
        return "admin/eventos";
    }

    @GetMapping("/eventos/novo")
    public String novoEvento(Model model) {
        prepararFormularioEvento(model, new EventoInput(null, "", "", null, "", ""));
        return "admin/evento-form";
    }

    @GetMapping("/eventos/{id}/editar")
    public String editarEvento(@PathVariable Integer id, Model model) {
        Evento evento = eventoService.buscar(id);
        prepararFormularioEvento(model, new EventoInput(evento.getOng().getId(), evento.getTitulo(),
                evento.getDescricao(), evento.getDataHora(), evento.getLocalEvento(), evento.getFotoUrl()));
        model.addAttribute("id", id);
        return "admin/evento-form";
    }

    @PostMapping("/eventos")
    public String criarEvento(
            @Valid @ModelAttribute("form") EventoInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormularioEvento(model, form);
            return "admin/evento-form";
        }
        eventoService.criar(form);
        redirect.addFlashAttribute("sucesso", "Evento cadastrado.");
        return "redirect:/admin/eventos";
    }

    @PostMapping("/eventos/{id}")
    public String atualizarEvento(
            @PathVariable Integer id,
            @Valid @ModelAttribute("form") EventoInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            prepararFormularioEvento(model, form);
            model.addAttribute("id", id);
            return "admin/evento-form";
        }
        eventoService.atualizar(id, form);
        redirect.addFlashAttribute("sucesso", "Evento atualizado.");
        return "redirect:/admin/eventos";
    }

    @PostMapping("/eventos/{id}/excluir")
    public String excluirEvento(@PathVariable Integer id, RedirectAttributes redirect) {
        eventoService.excluir(id);
        redirect.addFlashAttribute("sucesso", "Evento excluído.");
        return "redirect:/admin/eventos";
    }

    private void prepararFormularioUsuario(Model model, UsuarioInput form) {
        model.addAttribute("form", form);
        model.addAttribute("tipos", UsuarioTipo.values());
    }

    private void prepararFormularioCurso(Model model, CursoInput form) {
        model.addAttribute("form", form);
        model.addAttribute("ongs", ongService.listar());
        model.addAttribute("professores", professorRepository.findAll());
    }

    private void prepararFormularioEvento(Model model, EventoInput form) {
        model.addAttribute("form", form);
        model.addAttribute("ongs", ongService.listar());
    }
}
