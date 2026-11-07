package br.com.expansaotech.web;

import br.com.expansaotech.service.RegistroNaoEncontradoException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(assignableTypes = {AdminController.class, PortalController.class})
@Order(Ordered.LOWEST_PRECEDENCE)
public class MvcExceptionHandler {

    @ExceptionHandler(RegistroNaoEncontradoException.class)
    ModelAndView naoEncontrado(RegistroNaoEncontradoException exception) {
        return erro(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ModelAndView argumentoInvalido(IllegalArgumentException exception) {
        return erro(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ModelAndView conflito(DataIntegrityViolationException exception) {
        return erro(HttpStatus.CONFLICT, "A operação não pode ser concluída por conflito nos dados relacionados.");
    }

    private ModelAndView erro(HttpStatus status, String mensagem) {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(status);
        view.addObject("codigo", status.value());
        view.addObject("mensagem", mensagem);
        return view;
    }
}
