package es.ubu.lsi.SpringPythonAPI.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import es.ubu.lsi.SpringPythonAPI.entity.Pokemon;
import es.ubu.lsi.SpringPythonAPI.service.PokemonService;

@RestController
@RequestMapping("/api/pokemon")
public class PokemonController {

    private final PokemonService servicio;

    public PokemonController(PokemonService servicio) {
        this.servicio = servicio;
    }

    /* ---------- DTO ---------- */
    public static class PokemonDTO {
        private String  nombre;
        private Integer numero;
        private String  imagenUrl;

        public PokemonDTO() { }
        public PokemonDTO(String n, Integer num, String img) {
            this.nombre = n; this.numero = num; this.imagenUrl = img;
        }
        public String  getNombre()    { return nombre;    }
        public void    setNombre(String n)  { nombre = n; }
        public Integer getNumero()    { return numero;    }
        public void    setNumero(Integer num){ numero = num; }
        public String  getImagenUrl() { return imagenUrl; }
        public void    setImagenUrl(String url){ imagenUrl = url; }
    }

    /* ---------- ENDPOINTS ---------- */

    /* ----------  GUARDAR  ---------- */
    @PostMapping                 // POST /api/pokemon
    public ResponseEntity<Void> save(@RequestBody PokemonDTO dto,
                                     Principal principal){
        servicio.guardar(principal.getName(),
                         dto.getNombre(),
                         dto.getNumero(),
                         dto.getImagenUrl());
        return ResponseEntity.ok().build();
    }

    /* ----------  LISTAR  ---------- */
    @GetMapping                  // GET /api/pokemon
    public List<Pokemon> list(Principal principal){
        return servicio.listar(principal.getName());
    }

    /* ----------  BORRAR  ---------- */
    @DeleteMapping("/pokemon/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable Long id,
        Principal principal) {
        servicio.borrar(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}