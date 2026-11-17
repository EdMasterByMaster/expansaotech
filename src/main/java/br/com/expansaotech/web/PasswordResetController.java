package br.com.expansaotech.web;

import br.com.expansaotech.service.PasswordResetService;
import br.com.expansaotech.web.dto.ForgotPasswordForm;
import br.com.expansaotech.web.dto.ResetPasswordForm;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetController {
    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/esqueci-senha")
    public String esqueciSenha(Model model) {
        model.addAttribute("form", new ForgotPasswordForm(""));
        return "forgot-password";
    }

    @PostMapping("/esqueci-senha")
    public String solicitarRedefinicao(
            @Valid @ModelAttribute("form") ForgotPasswordForm form,
            BindingResult result) {
        if (result.hasErrors()) {
            return "forgot-password";
        }
        passwordResetService.solicitarRedefinicao(form.email());
        return "redirect:/esqueci-senha?solicitado";
    }

    @GetMapping("/redefinir-senha")
    public String redefinirSenha(
            @RequestParam(required = false) String token,
            Model model) {
        model.addAttribute("token", token == null ? "" : token);
        model.addAttribute("tokenValido", passwordResetService.tokenValido(token));
        model.addAttribute("form", new ResetPasswordForm("", ""));
        return "reset-password";
    }

    @PostMapping("/redefinir-senha")
    public String salvarNovaSenha(
            @RequestParam String token,
            @Valid @ModelAttribute("form") ResetPasswordForm form,
            BindingResult result,
            Model model) {
        if (!passwordResetService.tokenValido(token)) {
            model.addAttribute("tokenValido", false);
            model.addAttribute("token", "");
            return "reset-password";
        }

        if (!result.hasErrors() && !Objects.equals(form.senha(), form.confirmarSenha())) {
            result.rejectValue("confirmarSenha", "senha.confirmacao", "As senhas não conferem.");
        }
        if (!result.hasErrors() && form.senha().getBytes(StandardCharsets.UTF_8).length > 72) {
            result.rejectValue("senha", "senha.tamanho", "A senha excede o limite suportado pelo armazenamento.");
        }
        if (result.hasErrors()) {
            model.addAttribute("tokenValido", true);
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (!passwordResetService.redefinirSenha(token, form.senha())) {
            model.addAttribute("tokenValido", false);
            model.addAttribute("token", "");
            return "reset-password";
        }
        return "redirect:/login?senhaRedefinida";
    }
}
