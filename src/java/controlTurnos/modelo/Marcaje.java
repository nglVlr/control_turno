package controlTurnos.modelo;

public class Marcaje {

    private int idMarcaje;
    private int idEmpleado;
    private String fechaMarcaje;
    private String horaEntrada;
    private String horaDescanso1;
    private String horaDescanso2;
    private String horaSalida;
    private int entradaTarde;       // 0 = puntual, 1 = tarde (CU2-RN01: tarde si pasa 8:01 am)
    private String observaciones;

    // Campo extra para mostrar en pantalla
    private String nombreEmpleado;

    public Marcaje() {}

    public int getIdMarcaje() { return idMarcaje; }
    public void setIdMarcaje(int idMarcaje) { this.idMarcaje = idMarcaje; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getFechaMarcaje() { return fechaMarcaje; }
    public void setFechaMarcaje(String fechaMarcaje) { this.fechaMarcaje = fechaMarcaje; }

    public String getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; }

    public String getHoraDescanso1() { return horaDescanso1; }
    public void setHoraDescanso1(String horaDescanso1) { this.horaDescanso1 = horaDescanso1; }

    public String getHoraDescanso2() { return horaDescanso2; }
    public void setHoraDescanso2(String horaDescanso2) { this.horaDescanso2 = horaDescanso2; }

    public String getHoraSalida() { return horaSalida; }
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; }

    public int getEntradaTarde() { return entradaTarde; }
    public void setEntradaTarde(int entradaTarde) { this.entradaTarde = entradaTarde; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }
}
