package controlTurnos.modelo;

public class Area {

    private int idArea;
    private String nombreArea;
    private String descripcion;
    private int activo;

    public Area() {}

    public int getIdArea() { return idArea; }
    public void setIdArea(int idArea) { this.idArea = idArea; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
