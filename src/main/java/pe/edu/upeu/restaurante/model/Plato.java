package pe.edu.upeu.restaurante.model;

import java.math.BigDecimal;

/** Modelo de Plato, siguiendo el estilo de clases Java de SysVentas. */
public class Plato {
    private long id;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private boolean disponible;

    public Plato() {
    }

    public Plato(long id, String nombre, String categoria, BigDecimal precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.disponible = disponible;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

}
