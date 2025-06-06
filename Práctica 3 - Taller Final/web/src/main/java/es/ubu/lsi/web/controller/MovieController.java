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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import es.ubu.lsi.web.entity.Favorito;
import es.ubu.lsi.web.entity.Usuario;
import es.ubu.lsi.web.entity.Valoracion;
import es.ubu.lsi.web.repository.FavoritoRepository;
import es.ubu.lsi.web.repository.UsuarioRepository;
import es.ubu.lsi.web.repository.ValoracionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MovieController {

    /* Clave para indicar el éxito de la operación. */
    private static final String SUCCESS_KEY = "success";

    /* Clave para indicar el mensaje de respuesta. */
    private static final String MESSAGE_KEY = "message";
    
    
    /**
     * Clave de API de TMDB para acceder a los datos de películas.
     */
    @Value("${tmdb.api.key:}")
    private String tmdbApiKey;

    /**
     * Token de acceso de TMDB para autenticación.
     * Si se proporciona, se usará en lugar de la clave de API.
     */
    @Value("${tmdb.access.token:}")
    private String tmdbAccessToken;

    /**
     * RestTemplate para realizar solicitudes HTTP a la API de TMDB.
     * Se utiliza para obtener datos de películas y gestionar favoritos.
     */
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Repositorios para acceder a los datos de usuarios, favoritos y valoraciones.
     * Se inyectan mediante constructor para seguir las mejores prácticas de Spring.
     */
    private final UsuarioRepository usuarioRepository;

    /** Repositorio para acceder a los favoritos de los usuarios. */
    private final FavoritoRepository favoritoRepository;

    /** Repositorio para acceder a las valoraciones de las películas. */
    private final ValoracionRepository valoracionRepository;

    /**
     * Obtiene una lista de películas populares o busca películas por consulta.
     * Si se proporciona un token de acceso, se usa para autenticación; de lo contrario, se usa la clave de API.
     *
     * @param query la consulta de búsqueda (opcional)
     * @param page  el número de página para paginación (por defecto 1)
     * @return una lista de películas populares o los resultados de la búsqueda
     */
    @GetMapping("/movies")
    public ResponseEntity<Object> getMovies(
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

            org.springframework.http.HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(headers);

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

    /**
     * Obtiene los detalles de una película específica por su ID.
     * Si se proporciona un token de acceso, se usa para autenticación; de lo contrario, se usa la clave de API.
     *
     * @param movieId el ID de la película a obtener
     * @return los detalles de la película
     */
    @GetMapping("/movies/{movieId}")
    public ResponseEntity<Object> getMovie(@PathVariable int movieId) {
        String url = "https://api.themoviedb.org/3/movie/" + movieId;

        if (tmdbAccessToken != null && !tmdbAccessToken.isEmpty()) {
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setBearerAuth(tmdbAccessToken);

            org.springframework.http.HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(headers);

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

    /**
     * Permite al usuario dar like a una película, agregándola a sus favoritos.
     * Si ya está en favoritos, se elimina de la lista.
     *
     * @param movieId el ID de la película a agregar o quitar de favoritos
     * @param request la solicitud HTTP
     * @return un mapa con el resultado de la operación
     */
    @PostMapping("/movies/{movieId}/like")
    public ResponseEntity<Map<String, Object>> likeMovie(
        @PathVariable int movieId, 
        HttpServletRequest request) {
    
        Map<String, Object> response = new HashMap<>();
        
        /* Obtiene al usuario autenticado. */
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "No autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        String username = auth.getName();
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(username);
        
        if (!usuarioOpt.isPresent()) {
            response.put(SUCCESS_KEY, false);
            response.put(MESSAGE_KEY, "Usuario no encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
        Usuario usuario = usuarioOpt.get();

        /* Verifica si ya está en favoritos. */
        Optional<Favorito> favoritoExistente = favoritoRepository.findByUsuarioAndMovieId(usuario, movieId);
        
        if (favoritoExistente.isPresent()) {
            /* Quita de favoritos. */
            favoritoRepository.deleteByUsuarioAndMovieId(usuario, movieId);
            response.put(SUCCESS_KEY, true);
            response.put(MESSAGE_KEY, "Película quitada de favoritos");
            response.put("isLiked", false);
        } else {
            /* Obtiene datos de la película de TMDB. */
            try {
                String movieUrl = "https://api.themoviedb.org/3/movie/" + movieId;
                
                Object movieData;
                if (tmdbAccessToken != null && !tmdbAccessToken.isEmpty()) {
                    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                    headers.setBearerAuth(tmdbAccessToken);
                    org.springframework.http.HttpEntity<Object> entity = new org.springframework.http.HttpEntity<>(headers);
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
                    response.put(SUCCESS_KEY, true);
                    response.put(MESSAGE_KEY, "Película agregada a favoritos");
                    response.put("isLiked", true);
                }
            } catch (RestClientException e) {
                response.put(SUCCESS_KEY, false);
                response.put(MESSAGE_KEY, "Error al obtener datos de la película");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las películas favoritas del usuario autenticado.
     * Devuelve una lista de películas con sus detalles.
     *
     * @return una lista de películas favoritas del usuario
     */
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
        }).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("results", movies);
        response.put("total_results", movies.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Construye una URL con los parámetros de consulta.
     *
     * @param baseUrl la URL base
     * @param params  los parámetros de consulta a añadir
     * @return la URL completa con los parámetros
     */
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

    /**
     * Obtiene la valoración de una película por su ID.
     * Devuelve la valoración del usuario autenticado y las estadísticas globales.
     *
     * @param movieId el ID de la película
     * @return un mapa con la valoración del usuario, si ha valorado, la media y el total de valoraciones
     */
    @GetMapping("/movies/{movieId}/rating")
    public ResponseEntity<Object> getRating(@PathVariable int movieId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = auth != null && auth.isAuthenticated()
                        ? usuarioRepository.findByNombre(auth.getName()).orElse(null)
                        : null;

        /* Obtiene los datos del usuario (si está logeado). */
        final int[] userRating = {0};
        boolean hasRated  = false;
        if (usuario != null) {
            hasRated = valoracionRepository
                    .findByUsuarioAndMovieId(usuario, movieId)
                    .map(v -> { userRating[0] = v.getRating(); return true; })
                    .orElse(false);
        }

        /* Obtiene los datos globales. */
        List<Valoracion> todas = valoracionRepository.findByMovieId(movieId);
        double average = todas.isEmpty() ? 0 :
                        todas.stream().mapToInt(Valoracion::getRating).average().orElse(0);
        long total = todas.size();

        Map<String,Object> res = new HashMap<>();
        res.put("userRating",    userRating[0]);
        res.put("hasRated",      hasRated);
        res.put("averageRating", Math.round(average * 10) / 10.0);
        res.put("totalRatings",  total);

        return ResponseEntity.ok(res);
    }

    /**
     * Permite al usuario valorar una película.
     * Si ya ha valorado, actualiza la valoración; si no, crea una nueva.
     * Devuelve la media y el total de valoraciones de la película.
     *
     * @param movieId el ID de la película a valorar
     * @param body    el cuerpo de la solicitud con la valoración (rating)
     * @return un mapa con el resultado de la operación y las estadísticas de valoración
     */
    @PostMapping("/movies/{movieId}/rate")
    public ResponseEntity<Object> rateMovie(@PathVariable int movieId,
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

        /* Inserta o actualiza la valoración. */
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

        /* Calcula la media y el total. */
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