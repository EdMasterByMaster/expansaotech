package br.com.expansaotech.web;

import br.com.expansaotech.domain.Usuario;
import br.com.expansaotech.domain.UsuarioTipo;
import br.com.expansaotech.service.AccountRegistrationService;
import br.com.expansaotech.service.CepEndereco;
import br.com.expansaotech.service.CepService;
import br.com.expansaotech.web.dto.PerfilInput;
import br.com.expansaotech.web.dto.RegistrationInput;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AccountController {
    private final AccountRegistrationService registrationService;
    private final CepService cepService;

    public AccountController(AccountRegistrationService registrationService, CepService cepService) {
        this.registrationService = registrationService;
        this.cepService = cepService;
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("form", new RegistrationInput("", "", "", "", UsuarioTipo.VOLUNTARIO));
        model.addAttribute("tipos", new UsuarioTipo[] {UsuarioTipo.VOLUNTARIO, UsuarioTipo.ALUNO, UsuarioTipo.DOADOR});
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @Valid @ModelAttribute("form") RegistrationInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("tipos",
                    new UsuarioTipo[] {UsuarioTipo.VOLUNTARIO, UsuarioTipo.ALUNO, UsuarioTipo.DOADOR});
            return "cadastro";
        }
        try {
            registrationService.cadastrar(form);
            redirect.addFlashAttribute("mensagem",
                    "Cadastro recebido. Verifique seu e-mail para ativar a conta.");
            return "redirect:/cadastro";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("tipos",
                    new UsuarioTipo[] {UsuarioTipo.VOLUNTARIO, UsuarioTipo.ALUNO, UsuarioTipo.DOADOR});
            return "cadastro";
        } catch (IllegalStateException exception) {
            model.addAttribute("erro", exception.getMessage());
            model.addAttribute("tipos",
                    new UsuarioTipo[] {UsuarioTipo.VOLUNTARIO, UsuarioTipo.ALUNO, UsuarioTipo.DOADOR});
            return "cadastro";
        }
    }

    @GetMapping("/ativar-conta")
    public String ativar(@RequestParam(required = false) String token, RedirectAttributes redirect) {
        redirect.addFlashAttribute("ativacao",
                registrationService.ativar(token)
                        ? "Conta ativada. Agora você pode entrar e completar seu perfil."
                        : "O link de ativação é inválido ou expirou.");
        return "redirect:/login";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal Usuario usuario, Model model) {
        model.addAttribute("form", new PerfilInput(
                usuario.getCpf() == null ? "" : usuario.getCpf(),
                usuario.getCep() == null ? "" : usuario.getCep(),
                usuario.getLogradouro() == null ? "" : usuario.getLogradouro(),
                usuario.getNumero() == null ? "" : usuario.getNumero(),
                usuario.getComplemento(),
                usuario.getDataNascimento(),
                usuario.getTelefone() == null ? "" : usuario.getTelefone(),
                usuario.getAreasInteresse(), usuario.getDiasDisponiveis()));
        model.addAttribute("diasSemana", java.time.DayOfWeek.values());
        return "perfil";
    }

    @PostMapping("/perfil")
    public String completarPerfil(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @ModelAttribute("form") PerfilInput form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("diasSemana", java.time.DayOfWeek.values());
            return "perfil";
        }
        try {
            registrationService.completarPerfil(usuario.getId(), form);
            redirect.addFlashAttribute("mensagem", "Perfil atualizado.");
            return "redirect:/perfil";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("diasSemana", java.time.DayOfWeek.values());
            model.addAttribute("erro", exception.getMessage());
            return "perfil";
        }
    }

    @PostMapping(value = "/perfil", params = "buscarCep")
    public String buscarCep(
            @ModelAttribute("form") PerfilInput form,
            Model model) {
        try {
            CepEndereco endereco = cepService.buscar(form.cep());
            model.addAttribute("form", new PerfilInput(
                    form.cpf(), endereco.cep(), endereco.logradouro(), form.numero(),
                    endereco.complemento(), form.dataNascimento(), form.telefone(),
                    form.areasInteresse(), form.diasDisponiveis()));
            model.addAttribute("mensagem", "Endereço localizado. Confira os dados e informe o número.");
        } catch (IllegalArgumentException exception) {
            model.addAttribute("erro", exception.getMessage());
        } catch (RestClientException exception) {
            model.addAttribute("erro", "Não foi possível consultar o CEP agora. Preencha o endereço manualmente.");
        }
        model.addAttribute("diasSemana", java.time.DayOfWeek.values());
        return "perfil";
    }
}
