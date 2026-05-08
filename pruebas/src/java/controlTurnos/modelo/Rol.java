package controlTurnos.modelo;

public class Rol {

    private int idRol;
    private String nombreRol;
    private String descripcion;
    private int activo;

    public Rol() {}

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
