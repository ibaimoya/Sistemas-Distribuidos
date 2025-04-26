package es.ubu.lsi.SpringPythonAPI.controller;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.ubu.lsi.SpringPythonAPI.service.PokemonService;

@Controller
public class MisPokemonsController {

    private final PokemonService servicio;

    public MisPokemonsController(PokemonService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/mis-pokemons")
    public String lista(Model model, Principal principal) {
        model.addAttribute("pokemons", servicio.listar(principal.getName()));
        model.addAttribute("usuarioActual", principal.getName());
        return "misPokemons";
    }
}