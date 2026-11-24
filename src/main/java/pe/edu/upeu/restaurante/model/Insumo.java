package pe.edu.upeu.restaurante.model;

import java.math.BigDecimal;

/** Modelo de Insumo, siguiendo el estilo de clases Java de SysVentas. */
public class Insumo {
    private long id;
    private String nombre;
    private String unidad;
    private BigDecimal stock;
    private BigDecimal minimo;

    public Insumo() {
    }

    public Insumo(long id, String nombre, String unidad, BigDecimal stock, BigDecimal minimo) {
        this.id = id;
        this.nombre = nombre;
        this.unidad = unidad;
        this.stock = stock;
        this.minimo = minimo;
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

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public BigDecimal getStock() {
        return stock;
    }

    public void setStock(BigDecimal stock) {
        this.stock = stock;
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public void setMinimo(BigDecimal minimo) {
        this.minimo = minimo;
    }

}
