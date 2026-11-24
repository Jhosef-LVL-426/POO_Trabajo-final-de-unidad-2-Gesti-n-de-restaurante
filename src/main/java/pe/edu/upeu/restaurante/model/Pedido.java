package pe.edu.upeu.restaurante.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Modelo de Pedido, siguiendo el estilo de clases Java de SysVentas. */
public class Pedido {
    private long id;
    private LocalDateTime fecha;
    private Long mesaId;
    private String cliente;
    private BigDecimal total;
    private String estado;

    public Pedido() {
    }

    public Pedido(long id, LocalDateTime fecha, Long mesaId, String cliente, BigDecimal total, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.mesaId = mesaId;
        this.cliente = cliente;
        this.total = total;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Long getMesaId() {
        return mesaId;
    }

    public void setMesaId(Long mesaId) {
        this.mesaId = mesaId;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
