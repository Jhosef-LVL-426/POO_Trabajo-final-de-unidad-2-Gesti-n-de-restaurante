package pe.edu.upeu.restaurante.model;

/** Modelo de Mesa, siguiendo el estilo de clases Java de SysVentas. */
public class Mesa {
    private long id;
    private int numero;
    private int capacidad;
    private String estado;

    public Mesa() {
    }

    public Mesa(long id, int numero, int capacidad, String estado) {
        this.id = id;
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
