package controlTurnos.modelo;

public class AsignacionTurno {

    private int idAsignacion;
    private int idEmpleado;
    private int idTurno;
    private String fechaInicio;
    private String fechaFin;
    private String estado;
    private int idAdminAsigno;
    private String fechaRegistro;

    // Campos extra para pantalla (JOINs)
    private String nombreEmpleado;
    private String nombreTurno;
    private String nombreAdmin;

    public AsignacionTurno() {}

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdAdminAsigno() { return idAdminAsigno; }
    public void setIdAdminAsigno(int idAdminAsigno) { this.idAdminAsigno = idAdminAsigno; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getNombreTurno() { return nombreTurno; }
    public void setNombreTurno(String nombreTurno) { this.nombreTurno = nombreTurno; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }
}
