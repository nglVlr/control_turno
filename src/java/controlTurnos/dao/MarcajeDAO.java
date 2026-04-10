package controlTurnos.dao;

import controlTurnos.modelo.Marcaje;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class MarcajeDAO {

    // ─────────────────────────────────────────────────────────
    // OBTENER MARCAJE DEL DÍA ACTUAL
    // ─────────────────────────────────────────────────────────
    public Marcaje obtenerMarcajeHoy(int idEmpleado) {
        Marcaje marcaje = null;
        String sql = "SELECT id_marcaje, id_empleado, fecha_marcaje, hora_entrada, "
                   + "hora_descanso1, hora_descanso2, hora_salida, entrada_tarde, observaciones "
                   + "FROM marcajes "
                   + "WHERE id_empleado = ? AND fecha_marcaje = CURDATE()";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            if (rs.next()) {
                marcaje = mapear(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener marcaje hoy: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return marcaje;
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR ENTRADA — CU2 paso 5-7
    // RN01: puntual = antes o exactamente a las 8:00:00
    //       tarde   = 8:01:00 en adelante
    // Se usa LocalTime.of(8,1,0) como límite — cualquier hora
    // igual o posterior a las 8:01:00 es entrada tarde.
    // ─────────────────────────────────────────────────────────
    public boolean marcarEntrada(int idEmpleado) {
        String sql = "INSERT INTO marcajes (id_empleado, fecha_marcaje, hora_entrada, entrada_tarde) "
                   + "VALUES (?, CURDATE(), CURTIME(), ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            // RN01: tarde si la hora actual es >= 8:01:00
            LocalTime ahora = LocalTime.now();
            LocalTime inicioTarde = LocalTime.of(8, 1, 0);
            int tarde = (!ahora.isBefore(inicioTarde)) ? 1 : 0;

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
    // MARCAR DESCANSO 1 — CU2-FA03
    // La validación de orden se hace en el Servlet
    // El WHERE aquí es una segunda capa de seguridad en BD
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
    // MARCAR DESCANSO 2 — CU2-FA04
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
    // MARCAR SALIDA — CU2-FA07
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
    // MAPEAR ResultSet → Marcaje
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // CERRAR RECURSOS
    // ─────────────────────────────────────────────────────────
    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
