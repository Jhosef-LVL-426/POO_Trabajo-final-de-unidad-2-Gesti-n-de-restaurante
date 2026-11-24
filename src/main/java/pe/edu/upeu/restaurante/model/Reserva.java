package pe.edu.upeu.restaurante.model;

import java.time.LocalDate;

/** Modelo de Reserva, siguiendo el estilo de clases Java de SysVentas. */
public class Reserva {
    private long id;
    private String cliente;
    private String telefono;
    private LocalDate fecha;
    private String hora;
    private int personas;
    private String estado;

    public Reserva() {
    }

    public Reserva(long id, String cliente, String telefono, LocalDate fecha, String hora, int personas, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.telefono = telefono;
        this.fecha = fecha;
        this.hora = hora;
        this.personas = personas;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getPersonas() {
        return personas;
    }

    public void setPersonas(int personas) {
        this.personas = personas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
