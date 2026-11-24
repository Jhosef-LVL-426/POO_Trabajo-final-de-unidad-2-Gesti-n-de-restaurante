package pe.edu.upeu.restaurante.dto;

import java.math.BigDecimal;

/** Modelo de PedidoItemDTO, siguiendo el estilo de clases Java de SysVentas. */
public class PedidoItemDTO {
    private long platoId;
    private String plato;
    private int cantidad;
    private BigDecimal precio;
    private BigDecimal subtotal;

    public PedidoItemDTO() {
    }

    public PedidoItemDTO(long platoId, String plato, int cantidad, BigDecimal precio, BigDecimal subtotal) {
        this.platoId = platoId;
        this.plato = plato;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = subtotal;
    }

    public long getPlatoId() {
        return platoId;
    }

    public void setPlatoId(long platoId) {
        this.platoId = platoId;
    }

    public String getPlato() {
        return plato;
    }

    public void setPlato(String plato) {
        this.plato = plato;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

}
