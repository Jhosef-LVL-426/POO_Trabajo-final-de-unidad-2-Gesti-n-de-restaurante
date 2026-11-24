package pe.edu.upeu.restaurante.model;

public enum Perfil {
    ADMINISTRACION("Administración"), ATENCION("Atención");

    private final String nombre;

    Perfil(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
