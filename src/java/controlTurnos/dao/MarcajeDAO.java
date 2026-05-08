package controlTurnos.dao;

import controlTurnos.modelo.Marcaje;
import controlTurnos.modelo.Turno;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MarcajeDAO {

    // ─────────────────────────────────────────────────────────
    // VALIDAR HORARIO DEL TURNO
    // RN01 actualizado: tarde si llega 1 minuto después de
    // la hora_inicio del turno. El margen es +15 min antes
    // de la hora_inicio para no bloquear anticipos.
    // Se bloquea si está fuera de la ventana del turno.
    // Turno Nocturno (22:00-06:00) cruza medianoche — manejo especial.
    // ─────────────────────────────────────────────────────────
    public boolean estaEnHorario(Turno turno) {
        if (turno == null) return true; // sin turno asignado: permitir
        LocalTime ahora    = LocalTime.now();
        LocalTime inicio   = LocalTime.parse(turno.getHoraInicio());
        LocalTime fin      = LocalTime.parse(turno.getHoraFin());
        // Ventana: desde 15 min antes del inicio hasta la hora de fin
        LocalTime ventanaInicio = inicio.minusMinutes(15);

        if (inicio.isBefore(fin)) {
            // Turno normal (no cruza medianoche)
            return !ahora.isBefore(ventanaInicio) && ahora.isBefore(fin);
        } else {
            // Turno nocturno (cruza medianoche ej: 22:00-06:00)
            return !ahora.isBefore(ventanaInicio) || ahora.isBefore(fin);
        }
    }

    // ─────────────────────────────────────────────────────────
    // CALCULAR ENTRADA TARDE según turno
    // Tarde = llegó más de 1 minuto después de hora_inicio
    // ─────────────────────────────────────────────────────────
    private int calcularTarde(Turno turno) {
        if (turno == null) return 0;
        LocalTime ahora     = LocalTime.now();
        LocalTime inicioTarde = LocalTime.parse(turno.getHoraInicio()).plusMinutes(1);
        // Turno nocturno: solo es tarde si llegó después de las 22:01
        if (turno.getHoraInicio().equals("22:00:00")) {
            return ahora.isAfter(inicioTarde) || ahora.isBefore(LocalTime.of(6, 0)) ? 1 : 0;
        }
        return !ahora.isBefore(inicioTarde) ? 1 : 0;
    }

    // ─────────────────────────────────────────────────────────
    // OBTENER MARCAJE DE HOY
    // ─────────────────────────────────────────────────────────
    public Marcaje obtenerMarcajeHoy(int idEmpleado) {
        Marcaje marcaje = null;
        String sql = "SELECT id_marcaje, id_empleado, fecha_marcaje, hora_entrada, "
                   + "hora_descanso1, hora_descanso2, hora_salida, entrada_tarde, observaciones "
                   + "FROM marcajes WHERE id_empleado = ? AND fecha_marcaje = CURDATE()";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            if (rs.next()) marcaje = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al obtener marcaje hoy: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return marcaje;
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR ENTRADA — RN01 según turno del empleado
    // ─────────────────────────────────────────────────────────
    public boolean marcarEntrada(int idEmpleado, Turno turno) {
        String sql = "INSERT INTO marcajes (id_empleado, fecha_marcaje, hora_entrada, entrada_tarde) "
                   + "VALUES (?, CURDATE(), CURTIME(), ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            int tarde = calcularTarde(turno);
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            ps.setInt(2, tarde);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar entrada: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR DESCANSO 1
    // ─────────────────────────────────────────────────────────
    public boolean marcarDescanso1(int idEmpleado) {
        String sql = "UPDATE marcajes SET hora_descanso1 = CURTIME() "
                   + "WHERE id_empleado = ? AND fecha_marcaje = CURDATE() "
                   + "AND hora_entrada IS NOT NULL AND hora_descanso1 IS NULL";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar descanso1: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR DESCANSO 2
    // ─────────────────────────────────────────────────────────
    public boolean marcarDescanso2(int idEmpleado) {
        String sql = "UPDATE marcajes SET hora_descanso2 = CURTIME() "
                   + "WHERE id_empleado = ? AND fecha_marcaje = CURDATE() "
                   + "AND hora_descanso1 IS NOT NULL AND hora_descanso2 IS NULL";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar descanso2: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR SALIDA
    // ─────────────────────────────────────────────────────────
    public boolean marcarSalida(int idEmpleado) {
        String sql = "UPDATE marcajes SET hora_salida = CURTIME() "
                   + "WHERE id_empleado = ? AND fecha_marcaje = CURDATE() "
                   + "AND hora_descanso2 IS NOT NULL AND hora_salida IS NULL";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al marcar salida: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR MARCAJES HOY POR ADMINAREA — AdminArea ve solo SUS empleados
    // ─────────────────────────────────────────────────────────
    public List<Marcaje> listarMarcajesHoyPorAdminArea(int idAdminArea) {
        List<Marcaje> lista = new ArrayList<>();
        String sql = "SELECT m.id_marcaje, m.id_empleado, m.fecha_marcaje, "
                   + "m.hora_entrada, m.hora_descanso1, m.hora_descanso2, "
                   + "m.hora_salida, m.entrada_tarde, m.observaciones, "
                   + "e.nombre_completo "
                   + "FROM marcajes m "
                   + "INNER JOIN empleados e ON m.id_empleado = e.id_empleado "
                   + "WHERE m.fecha_marcaje = CURDATE() "
                   + "AND e.id_admin_area = ? "
                   + "ORDER BY e.nombre_completo";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idAdminArea);
            rs = ps.executeQuery();
            while (rs.next()) {
                Marcaje m = mapear(rs);
                m.setNombreEmpleado(rs.getString("nombre_completo"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar marcajes: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR TODOS LOS MARCAJES DE HOY — solo para AdminRRHH (visual)
    // ─────────────────────────────────────────────────────────
    public List<Marcaje> listarTodosMarcajesHoy() {
        List<Marcaje> lista = new ArrayList<>();
        String sql = "SELECT m.id_marcaje, m.id_empleado, m.fecha_marcaje, "
                   + "m.hora_entrada, m.hora_descanso1, m.hora_descanso2, "
                   + "m.hora_salida, m.entrada_tarde, m.observaciones, "
                   + "e.nombre_completo "
                   + "FROM marcajes m "
                   + "INNER JOIN empleados e ON m.id_empleado = e.id_empleado "
                   + "WHERE m.fecha_marcaje = CURDATE() "
                   + "ORDER BY e.nombre_completo";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Marcaje m = mapear(rs);
                m.setNombreEmpleado(rs.getString("nombre_completo"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar todos marcajes: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    private Marcaje mapear(ResultSet rs) throws SQLException {
        Marcaje m = new Marcaje();
        m.setIdMarcaje(rs.getInt("id_marcaje"));
        m.setIdEmpleado(rs.getInt("id_empleado"));
        m.setFechaMarcaje(rs.getString("fecha_marcaje"));
        m.setHoraEntrada(rs.getString("hora_entrada"));
        m.setHoraDescanso1(rs.getString("hora_descanso1"));
        m.setHoraDescanso2(rs.getString("hora_descanso2"));
        m.setHoraSalida(rs.getString("hora_salida"));
        m.setEntradaTarde(rs.getInt("entrada_tarde"));
        m.setObservaciones(rs.getString("observaciones"));
        return m;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
