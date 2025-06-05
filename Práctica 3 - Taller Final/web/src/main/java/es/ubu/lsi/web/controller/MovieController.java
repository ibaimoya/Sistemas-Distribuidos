package es.ubu.lsi.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import es.ubu.lsi.web.entity.Favorito;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.entity.Valoracion;
import es.ubu.lsi.web.repository.FavoritoRepository;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.repository.ValoracionRepository;
import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api")
public class MovieController {

    @Value("${tmdb.api.key:}")
    private String tmdbApiKey;

    @Value("${tmdb.access.token:}")
    private String tmdbAccessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    private final UsuarioRepository usuarioRepository;
    private final FavoritoRepository favoritoRepository;
    private final ValoracionRepository valoracionRepository;

    public MovieController(UsuarioRepository usuarioRepo,
                        FavoritoRepository favoritoRepo,
                        ValoracionRepository valoracionRepo) {
        this.usuarioRepository   = usuarioRepo;
        this.favoritoRepository  = favoritoRepo;
        this.valoracionRepository = valoracionRepo;
    }

    @GetMapping("/movies")
    public ResponseEntity<?> getMovies(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "1") int page) {

        String url;
        Map<String, Object> params = new HashMap<>();

        if (query != null && !query.trim().isEmpty()) {
            url = "https://api.themoviedb.org/3/search/movie";
            params.put("query", query);
            params.put("include_adult", false);
        } else {
            url = "https://api.themoviedb.org/3/movie/popular";
        }

        params.put("page", Math.max(1, page));

        if (tmdbAccessToken != null && !tmdbAccessToken.isEmpty()) {
            // Usar Bearer token si está disponible
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(tmdbAccessToken);

            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);

            String finalUrl = buildUrlWithParams(url, params);
            return ResponseEntity.ok(restTemplate.exchange(
                    finalUrl,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Object.class).getBody());
        } else {
            // Usar API key como parámetro
            params.put("api_key", tmdbApiKey);
            String finalUrl = buildUrlWithParams(url, params);
            return ResponseEntity.ok(restTemplate.getForObject(finalUrl, Object.class));
        }
    }

    @GetMapping("/movies/{movieId}")
    public ResponseEntity<?> getMovie(@PathVariable int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId;

        if (tmdbAccessToken != null && !tmdbAccessToken.isEmpty()) {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(tmdbAccessToken);

            org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);

            return ResponseEntity.ok(restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Object.class).getBody());
        } else {
            url += "?api_key=" + tmdbApiKey;
            return ResponseEntity.ok(restTemplate.getForObject(url, Object.class));
        }
    }

    @PostMapping("/movies/{movieId}/like")
    public ResponseEntity<Map<String, Object>> likeMovie(
        @PathVariable int movieId, 
        HttpServletRequest request) {
    
        Map<String, Object> response = new HashMap<>();
        
        // Obtener usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.put("success", false);
            response.put("message", "No autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String username = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(username);
        
        if (!usuarioOpt.isPresent()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // Verificar si ya está en favoritos
        Optional<Favorito> favoritoExistente = favoritoRepository.findByUsuarioAndMovieId(usuario, movieId);
        
        if (favoritoExistente.isPresent()) {
            // Quitar de favoritos
            favoritoRepository.deleteByUsuarioAndMovieId(usuario, movieId);
            response.put("success", true);
            response.put("message", "Película quitada de favoritos");
            response.put("isLiked", false);
        } else {
            // Obtener datos de la película de TMDB
            try {
                String movieUrl = "https://api.themoviedb.org/3/movie/" + movieId;
                
                Object movieData;
                if (tmdbAccessToken != null && !tmdbAccessToken.isEmpty()) {
                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    headers.setBearerAuth(tmdbAccessToken);
                    org.springframework.http.HttpEntity<?> entity = new org.springframework.http.HttpEntity<>(headers);
                    movieData = restTemplate.exchange(movieUrl, org.springframework.http.HttpMethod.GET, entity, Object.class).getBody();
                } else {
                    movieUrl += "?api_key=" + tmdbApiKey;
                    movieData = restTemplate.getForObject(movieUrl, Object.class);
                }
                
                if (movieData instanceof Map) {
                    Map<String, Object> movie = (Map<String, Object>) movieData;
                    
                    Favorito favorito = new Favorito(
                        usuario,
                        movieId,
                        (String) movie.get("title"),
                        (String) movie.get("poster_path"),
                        (String) movie.get("overview")
                    );
                    
                    favoritoRepository.save(favorito);
                    response.put("success", true);
                    response.put("message", "Película agregada a favoritos");
                    response.put("isLiked", true);
                }
            } catch (Exception e) {
                response.put("success", false);
                response.put("message", "Error al obtener datos de la película");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movies/favorites")
    public ResponseEntity<Map<String, Object>> getFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        String username = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(username);
        
        if (!usuarioOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Favorito> favoritos = favoritoRepository.findByUsuarioOrderByIdDesc(usuarioOpt.get());
        
        List<Map<String, Object>> movies = favoritos.stream().map(fav -> {
            Map<String, Object> movie = new HashMap<>();
            movie.put("id", fav.getMovieId());
            movie.put("title", fav.getTitle());
            movie.put("poster_path", fav.getPosterPath());
            movie.put("overview", fav.getOverview());
            return movie;
        }).collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", movies);
        response.put("total_results", movies.size());
        
        return ResponseEntity.ok(response);
    }

    private String buildUrlWithParams(String baseUrl, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(baseUrl).append("?");

        params.forEach((key, value) -> {
            if (value != null) {
                url.append(key).append("=").append(value).append("&");
            }
        });

        if (url.length() > 0 && url.charAt(url.length() - 1) == '&') {
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }

    @GetMapping("/movies/{movieId}/rating")
    public ResponseEntity<?> getRating(@PathVariable int movieId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = auth != null && auth.isAuthenticated()
                        ? usuarioRepository.findByNombre(auth.getName()).orElse(null)
                        : null;

        // Datos del usuario (si está logeado)
        final int[] userRating = {0};
        boolean hasRated  = false;
        if (usuario != null) {
            hasRated = valoracionRepository
                    .findByUsuarioAndMovieId(usuario, movieId)
                    .map(v -> { userRating[0] = v.getRating(); return true; })
                    .orElse(false);
        }

        // Datos globales
        List<Valoracion> todas = valoracionRepository.findByMovieId(movieId);
        double average = todas.isEmpty() ? 0 :
                        todas.stream().mapToInt(Valoracion::getRating).average().orElse(0);
        long total = todas.size();

        Map<String,Object> res = new HashMap<>();
        res.put("userRating",    userRating[0]);
        res.put("hasRated",      hasRated);
        res.put("averageRating", Math.round(average * 10) / 10.0); // 1 decimal
        res.put("totalRatings",  total);

        return ResponseEntity.ok(res);
    }


    @PostMapping("/movies/{movieId}/rate")
    public ResponseEntity<?> rateMovie(@PathVariable int movieId,
                                    @RequestBody Map<String,Integer> body) {

        int rating = body.getOrDefault("rating", 0);
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest()
                                .body(Map.of("success",false,"message","Rating fuera de rango"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Usuario usuario = usuarioRepository.findByNombre(auth.getName()).orElse(null);
        if (usuario == null)
            return ResponseEntity.badRequest().build();

        // Insertar o actualizar
        String action;
        Optional<Valoracion> existente =
            valoracionRepository.findByUsuarioAndMovieId(usuario, movieId);

        if (existente.isPresent()) {
            existente.get().setRating(rating);
            valoracionRepository.save(existente.get());
            action = "updated";
        } else {
            valoracionRepository.save(new Valoracion(usuario, movieId, rating));
            action = "created";
        }

        // Calcular media y total
        double average = valoracionRepository.findByMovieId(movieId)
                        .stream().mapToInt(Valoracion::getRating).average().orElse(0);
        long total = valoracionRepository.countByMovieId(movieId);

        Map<String,Object> res = new HashMap<>();
        res.put("success", true);
        res.put("action",  action);
        res.put("averageRating", Math.round(average * 10) / 10.0);
        res.put("totalRatings",  total);

        return ResponseEntity.ok(res);
    }
}