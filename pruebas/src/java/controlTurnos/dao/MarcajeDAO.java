package controlTurnos.dao;

import controlTurnos.modelo.Marcaje;
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
    // OBTENER MARCAJE DE HOY — para mostrar estado en pantalla
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
    // MARCAR ENTRADA — CU2 paso 5, RN01
    // Puntual: antes de las 8:01 — Tarde: 8:01 en adelante
    // ─────────────────────────────────────────────────────────
    public boolean marcarEntrada(int idEmpleado) {
        String sql = "INSERT INTO marcajes (id_empleado, fecha_marcaje, hora_entrada, entrada_tarde) "
                   + "VALUES (?, CURDATE(), CURTIME(), ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
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
    // LISTAR MARCAJES DEL DÍA — AdminArea ve solo su área y turno
    // Nuevo requerimiento: AdminArea solo ve su área y turno
    // ─────────────────────────────────────────────────────────
    public List<Marcaje> listarMarcajesHoyPorAreaYTurno(int idArea, int idTurno) {
        List<Marcaje> lista = new ArrayList<>();
        String sql = "SELECT m.id_marcaje, m.id_empleado, m.fecha_marcaje, "
                   + "m.hora_entrada, m.hora_descanso1, m.hora_descanso2, "
                   + "m.hora_salida, m.entrada_tarde, m.observaciones, "
                   + "e.nombre_completo "
                   + "FROM marcajes m "
                   + "INNER JOIN empleados e ON m.id_empleado = e.id_empleado "
                   + "WHERE m.fecha_marcaje = CURDATE() "
                   + "AND e.id_area = ? AND e.id_turno_default = ? "
                   + "ORDER BY e.nombre_completo";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            ps.setInt(2, idTurno);
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
