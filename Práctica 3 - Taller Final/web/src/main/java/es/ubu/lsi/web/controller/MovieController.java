package es.ubu.lsi.web.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MovieController {

    @Value("${tmdb.api.key:}")
    private String tmdbApiKey;

    @Value("${tmdb.access.token:}")
    private String tmdbAccessToken;

    private final RestTemplate restTemplate = new RestTemplate();

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

    @GetMapping("/movie/{movieId}")
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
    public ResponseEntity<Map<String, Object>> likeMovie(@PathVariable int movieId) {
        // Aquí se puede implementar la lógica para guardar "likes" en la base de datos
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Película marcada como favorita");
        response.put("movieId", movieId);

        return ResponseEntity.ok(response);
    }

    private String buildUrlWithParams(String baseUrl, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(baseUrl).append("?");

        params.forEach((key, value) -> {
            if (value != null) {
                url.append(key).append("=").append(value).append("&");
            }
        });

        // Eliminar el último &
        if (url.length() > 0 && url.charAt(url.length() - 1) == '&') {
            url.setLength(url.length() - 1);
        }

        return url.toString();
    }
}