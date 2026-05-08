package controlTurnos.modelo;

public class Empleado {

    private int    idEmpleado;
    private String dpi;
    private String nombreCompleto;
    private String usuario;
    private String contrasena;
    private String correo;
    private int    idArea;
    private int    idRol;
    private int    idTurnoDefault;
    private int    idAdminArea;
    private String estado;
    private String motivoInactivacion;
    private int    diasVacaciones;
    private String fechaCreacion;
    private int    creadoPor;
    private int    modificadoPor;

    // Campos extra para pantalla (JOINs)
    private String nombreArea;
    private String nombreRol;
    private String nombreTurno;
    private String nombreAdminArea;
    private String creadoPorNombre;
    private String modificadoPorNombre;

    public Empleado() {}

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int v) { this.idEmpleado = v; }

    public String getDpi() { return dpi; }
    public void setDpi(String v) { this.dpi = v; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String v) { this.nombreCompleto = v; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String v) { this.usuario = v; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String v) { this.contrasena = v; }

    public String getCorreo() { return correo; }
    public void setCorreo(String v) { this.correo = v; }

    public int getIdArea() { return idArea; }
    public void setIdArea(int v) { this.idArea = v; }

    public int getIdRol() { return idRol; }
    public void setIdRol(int v) { this.idRol = v; }

    public int getIdTurnoDefault() { return idTurnoDefault; }
    public void setIdTurnoDefault(int v) { this.idTurnoDefault = v; }

    public int getIdAdminArea() { return idAdminArea; }
    public void setIdAdminArea(int v) { this.idAdminArea = v; }

    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }

    public String getMotivoInactivacion() { return motivoInactivacion; }
    public void setMotivoInactivacion(String v) { this.motivoInactivacion = v; }

    public int getDiasVacaciones() { return diasVacaciones; }
    public void setDiasVacaciones(int v) { this.diasVacaciones = v; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String v) { this.fechaCreacion = v; }

    public int getCreadoPor() { return creadoPor; }
    public void setCreadoPor(int v) { this.creadoPor = v; }

    public int getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(int v) { this.modificadoPor = v; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String v) { this.nombreArea = v; }

    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String v) { this.nombreRol = v; }

    public String getNombreTurno() { return nombreTurno; }
    public void setNombreTurno(String v) { this.nombreTurno = v; }

    public String getNombreAdminArea() { return nombreAdminArea; }
    public void setNombreAdminArea(String v) { this.nombreAdminArea = v; }

    public String getCreadoPorNombre() { return creadoPorNombre; }
    public void setCreadoPorNombre(String v) { this.creadoPorNombre = v; }

    public String getModificadoPorNombre() { return modificadoPorNombre; }
    public void setModificadoPorNombre(String v) { this.modificadoPorNombre = v; }
}
