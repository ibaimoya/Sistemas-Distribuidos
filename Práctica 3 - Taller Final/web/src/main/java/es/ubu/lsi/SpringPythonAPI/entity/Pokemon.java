package es.ubu.lsi.SpringPythonAPI.entity;

import javax.persistence.*;

/**
 * Clase que representa un Pokemon.
 * 
 * @author Ibai Moya Aroz
 *
 * @version 1.0
 * @since 1.0 
 */
@Entity
public class Pokemon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "numero", nullable = true)
    private Integer numero;

    @Column(name = "imagen_url", length = 255, nullable = true)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Usuario usuario;

    public Pokemon() {}

    public Pokemon(String nombre, Integer numero, String imagenUrl, Usuario usuario) {
        this.nombre = nombre;
        this.numero = numero;
        this.imagenUrl = imagenUrl;
        this.usuario = usuario;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}