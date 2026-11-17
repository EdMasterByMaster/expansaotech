package br.com.expansaotech.web;

import br.com.expansaotech.service.CursoService;
import br.com.expansaotech.service.EventoService;
import br.com.expansaotech.service.OngService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PortalController {
    private final OngService ongService;
    private final CursoService cursoService;
    private final EventoService eventoService;

    public PortalController(OngService ongService, CursoService cursoService, EventoService eventoService) {
        this.ongService = ongService;
        this.cursoService = cursoService;
        this.eventoService = eventoService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("ongs", ongService.listar());
        model.addAttribute("cursos", cursoService.listar());
        model.addAttribute("eventos", eventoService.listar());
        return "index";
    }

    @GetMapping("/cursos")
    public String cursos(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        return "cursos";
    }

    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("eventos", eventoService.listar());
        return "eventos";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
