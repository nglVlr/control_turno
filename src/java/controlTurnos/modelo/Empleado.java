package controlTurnos.modelo;

public class Empleado {

    private int idEmpleado;
    private String dpi;
    private String nombreCompleto;
    private String usuario;
    private String contrasena;
    private String correo;
    private int idArea;
    private int idRol;
    private int idTurnoDefault;
    private String estado;
    private String motivoInactivacion;
    private int diasVacaciones;
    private String fechaCreacion;

    // Campos extra para mostrar en pantalla (joins)
    private String nombreArea;
    private String nombreRol;
    private String nombreTurno;

    public Empleado() {}

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public int getIdArea() { return idArea; }
    public void setIdArea(int idArea) { this.idArea = idArea; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int idRol) { this.idRol = idRol; }

    public int getIdTurnoDefault() { return idTurnoDefault; }
    public void setIdTurnoDefault(int idTurnoDefault) { this.idTurnoDefault = idTurnoDefault; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMotivoInactivacion() { return motivoInactivacion; }
    public void setMotivoInactivacion(String motivoInactivacion) { this.motivoInactivacion = motivoInactivacion; }

    public int getDiasVacaciones() { return diasVacaciones; }
    public void setDiasVacaciones(int diasVacaciones) { this.diasVacaciones = diasVacaciones; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }

    public String getNombreTurno() { return nombreTurno; }
    public void setNombreTurno(String nombreTurno) { this.nombreTurno = nombreTurno; }
}
