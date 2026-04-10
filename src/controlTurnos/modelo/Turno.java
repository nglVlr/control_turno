package controlTurnos.modelo;

public class Turno {

    private int idTurno;
    private String nombreTurno;
    private String horaInicio;
    private String horaFin;
    private int horasDuracion;
    private int activo;

    public Turno() {}

    public int getIdTurno() { return idTurno; }
    public void setIdTurno(int idTurno) { this.idTurno = idTurno; }

    public String getNombreTurno() { return nombreTurno; }
    public void setNombreTurno(String nombreTurno) { this.nombreTurno = nombreTurno; }

    public String getHoraInicio() { return horaInicio; }
    public void setHoraInicio(String horaInicio) { this.horaInicio = horaInicio; }

    public String getHoraFin() { return horaFin; }
    public void setHoraFin(String horaFin) { this.horaFin = horaFin; }

    public int getHorasDuracion() { return horasDuracion; }
    public void setHorasDuracion(int horasDuracion) { this.horasDuracion = horasDuracion; }

    public int getActivo() { return activo; }
    public void setActivo(int activo) { this.activo = activo; }
}
