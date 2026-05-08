package controlTurnos.modelo;

public class SolicitudGestion {

    private int    idSolicitud;
    private int    idEmpleado;
    private int    idTipoGestion;
    private String fechaInicio;
    private String fechaFin;
    private String motivo;
    private String estado;
    private int    idAdminResolvio;
    private String fechaResolucion;
    private String observacionAdmin;
    private String fechaCreacion;

    // Campos extra para pantalla (JOINs)
    private String nombreEmpleado;
    private String nombreTipoGestion;
    private String nombreAdmin;
    private String nombreArea;

    public SolicitudGestion() {}

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public int getIdTipoGestion() { return idTipoGestion; }
    public void setIdTipoGestion(int idTipoGestion) { this.idTipoGestion = idTipoGestion; }

    public String getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio = fechaInicio; }

    public String getFechaFin() { return fechaFin; }
    public void setFechaFin(String fechaFin) { this.fechaFin = fechaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdAdminResolvio() { return idAdminResolvio; }
    public void setIdAdminResolvio(int idAdminResolvio) { this.idAdminResolvio = idAdminResolvio; }

    public String getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(String fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getObservacionAdmin() { return observacionAdmin; }
    public void setObservacionAdmin(String observacionAdmin) { this.observacionAdmin = observacionAdmin; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getNombreTipoGestion() { return nombreTipoGestion; }
    public void setNombreTipoGestion(String nombreTipoGestion) { this.nombreTipoGestion = nombreTipoGestion; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }
}
