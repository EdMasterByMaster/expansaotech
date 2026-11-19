package br.com.expansaotech.web;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.service.ParticipacaoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ParticipacaoController {
    private final ParticipacaoService participacaoService;

    public ParticipacaoController(ParticipacaoService participacaoService) {
        this.participacaoService = participacaoService;
    }

    @PostMapping("/cursos/{id}/inscricao")
    public String matricular(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario,
            RedirectAttributes redirect) {
        try {
            participacaoService.matricular(id, usuario.getId());
            redirect.addFlashAttribute("mensagem", "Inscrição realizada com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/cursos";
    }

    @PostMapping("/eventos/{id}/inscricao")
    public String inscreverEvento(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario,
            RedirectAttributes redirect) {
        try {
            participacaoService.inscreverEvento(id, usuario.getId());
            redirect.addFlashAttribute("mensagem", "Inscrição realizada com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/eventos";
    }
}
