package es.ubu.lsi.web.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer movieId;

    @Column(nullable = false)
    private String title;

    @Column
    private String posterPath;

    @Column(length = 1000)
    private String overview;

    public Favorito() {}

    public Favorito(Usuario usuario, Integer movieId, String title, String posterPath, String overview) {
        this.usuario = usuario;
        this.movieId = movieId;
        this.title = title;
        this.posterPath = posterPath;
        this.overview = overview;
    }

    // Getters y setters
    public Long getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Integer getMovieId() { return movieId; }
    public void setMovieId(Integer movieId) { this.movieId = movieId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
}