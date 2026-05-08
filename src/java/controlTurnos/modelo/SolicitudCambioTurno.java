package controlTurnos.modelo;

public class SolicitudCambioTurno {

    private int    idSolicitudCt;
    private int    idEmpleado;
    private String fechaInicial;
    private int    idTurnoInicial;
    private int    idAreaOrigen;
    private String fechaNueva;
    private int    idTurnoNuevo;
    private int    idAreaDestino;
    private String justificacion;
    private String estado;
    private String notifAdminOrigen;
    private String notifAdminDestino;
    private int    idRrhhResolvio;
    private String fechaResolucion;
    private String observacionRrhh;
    private String fechaCreacion;

    // Campos extra para pantalla (JOINs)
    private String nombreEmpleado;
    private String nombreTurnoInicial;
    private String nombreTurnoNuevo;
    private String nombreAreaOrigen;
    private String nombreAreaDestino;
    private String nombreRrhh;

    public SolicitudCambioTurno() {}

    public int getIdSolicitudCt() { return idSolicitudCt; }
    public void setIdSolicitudCt(int idSolicitudCt) { this.idSolicitudCt = idSolicitudCt; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getFechaInicial() { return fechaInicial; }
    public void setFechaInicial(String fechaInicial) { this.fechaInicial = fechaInicial; }

    public int getIdTurnoInicial() { return idTurnoInicial; }
    public void setIdTurnoInicial(int idTurnoInicial) { this.idTurnoInicial = idTurnoInicial; }

    public int getIdAreaOrigen() { return idAreaOrigen; }
    public void setIdAreaOrigen(int idAreaOrigen) { this.idAreaOrigen = idAreaOrigen; }

    public String getFechaNueva() { return fechaNueva; }
    public void setFechaNueva(String fechaNueva) { this.fechaNueva = fechaNueva; }

    public int getIdTurnoNuevo() { return idTurnoNuevo; }
    public void setIdTurnoNuevo(int idTurnoNuevo) { this.idTurnoNuevo = idTurnoNuevo; }

    public int getIdAreaDestino() { return idAreaDestino; }
    public void setIdAreaDestino(int idAreaDestino) { this.idAreaDestino = idAreaDestino; }

    public String getJustificacion() { return justificacion; }
    public void setJustificacion(String justificacion) { this.justificacion = justificacion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNotifAdminOrigen() { return notifAdminOrigen; }
    public void setNotifAdminOrigen(String notifAdminOrigen) { this.notifAdminOrigen = notifAdminOrigen; }

    public String getNotifAdminDestino() { return notifAdminDestino; }
    public void setNotifAdminDestino(String notifAdminDestino) { this.notifAdminDestino = notifAdminDestino; }

    public int getIdRrhhResolvio() { return idRrhhResolvio; }
    public void setIdRrhhResolvio(int idRrhhResolvio) { this.idRrhhResolvio = idRrhhResolvio; }

    public String getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(String fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getObservacionRrhh() { return observacionRrhh; }
    public void setObservacionRrhh(String observacionRrhh) { this.observacionRrhh = observacionRrhh; }

    public String getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getNombreTurnoInicial() { return nombreTurnoInicial; }
    public void setNombreTurnoInicial(String nombreTurnoInicial) { this.nombreTurnoInicial = nombreTurnoInicial; }

    public String getNombreTurnoNuevo() { return nombreTurnoNuevo; }
    public void setNombreTurnoNuevo(String nombreTurnoNuevo) { this.nombreTurnoNuevo = nombreTurnoNuevo; }

    public String getNombreAreaOrigen() { return nombreAreaOrigen; }
    public void setNombreAreaOrigen(String nombreAreaOrigen) { this.nombreAreaOrigen = nombreAreaOrigen; }

    public String getNombreAreaDestino() { return nombreAreaDestino; }
    public void setNombreAreaDestino(String nombreAreaDestino) { this.nombreAreaDestino = nombreAreaDestino; }

    public String getNombreRrhh() { return nombreRrhh; }
    public void setNombreRrhh(String nombreRrhh) { this.nombreRrhh = nombreRrhh; }
}
