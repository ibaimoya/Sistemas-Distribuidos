package es.ubu.lsi.SpringPythonAPI.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class ErrorController {

  /* Maneja el error 404. */
  @ExceptionHandler(NoHandlerFoundException.class)
  public String paginaNoEncontrada(Model model) {
    model.addAttribute("mensaje", "Página no encontrada.");
    return "error/404";
  }

  /* Maneja el resto de errores. */
  @ExceptionHandler(Exception.class)
  public String errorGeneral(Model model, Exception ex) {
    model.addAttribute("mensaje", ex.getMessage());
    return "error"; 
  }
}