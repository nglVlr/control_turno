package controlTurnos.dao;

import controlTurnos.modelo.AsignacionTurno;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AsignacionTurnoDAO {

    // ─────────────────────────────────────────────────────────
    // ASIGNAR TURNO — CU3 paso 10
    // AdminArea solo puede asignar turnos a empleados de
    // su misma área y turno (validado en el Servlet)
    // ─────────────────────────────────────────────────────────
    public boolean asignar(AsignacionTurno at) {
        // Cancelar asignación vigente anterior del empleado antes de crear la nueva
        cancelarVigente(at.getIdEmpleado());

        String sql = "INSERT INTO asignacion_turnos "
                   + "(id_empleado, id_turno, fecha_inicio, fecha_fin, estado, id_admin_asigno) "
                   + "VALUES (?, ?, ?, ?, 'Vigente', ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, at.getIdEmpleado());
            ps.setInt(2, at.getIdTurno());
            ps.setString(3, at.getFechaInicio());
            ps.setString(4, at.getFechaFin());
            ps.setInt(5, at.getIdAdminAsigno());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al asignar turno: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // CANCELAR VIGENTE — antes de crear nueva asignación
    // ─────────────────────────────────────────────────────────
    private void cancelarVigente(int idEmpleado) {
        String sql = "UPDATE asignacion_turnos SET estado = 'Modificada' "
                   + "WHERE id_empleado = ? AND estado = 'Vigente'";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al cancelar vigente: " + e.getMessage());
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR POR AREA Y TURNO — AdminArea ve solo las de su área y turno
    // ─────────────────────────────────────────────────────────
    public List<AsignacionTurno> listarPorAreaYTurno(int idArea, int idTurno) {
        List<AsignacionTurno> lista = new ArrayList<>();
        String sql = "SELECT at.id_asignacion, at.id_empleado, at.id_turno, "
                   + "at.fecha_inicio, at.fecha_fin, at.estado, at.id_admin_asigno, "
                   + "at.fecha_registro, e.nombre_completo, t.nombre_turno, "
                   + "adm.nombre_completo AS nombre_admin "
                   + "FROM asignacion_turnos at "
                   + "INNER JOIN empleados e   ON at.id_empleado    = e.id_empleado "
                   + "INNER JOIN turnos t      ON at.id_turno       = t.id_turno "
                   + "INNER JOIN empleados adm ON at.id_admin_asigno = adm.id_empleado "
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? "
                   + "ORDER BY at.fecha_registro DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            ps.setInt(2, idTurno);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar asignaciones: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR TODOS — AdminRRHH ve todas las asignaciones
    // ─────────────────────────────────────────────────────────
    public List<AsignacionTurno> listarTodos() {
        List<AsignacionTurno> lista = new ArrayList<>();
        String sql = "SELECT at.id_asignacion, at.id_empleado, at.id_turno, "
                   + "at.fecha_inicio, at.fecha_fin, at.estado, at.id_admin_asigno, "
                   + "at.fecha_registro, e.nombre_completo, t.nombre_turno, "
                   + "adm.nombre_completo AS nombre_admin "
                   + "FROM asignacion_turnos at "
                   + "INNER JOIN empleados e   ON at.id_empleado    = e.id_empleado "
                   + "INNER JOIN turnos t      ON at.id_turno       = t.id_turno "
                   + "INNER JOIN empleados adm ON at.id_admin_asigno = adm.id_empleado "
                   + "ORDER BY at.fecha_registro DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar asignaciones: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    private AsignacionTurno mapear(ResultSet rs) throws SQLException {
        AsignacionTurno at = new AsignacionTurno();
        at.setIdAsignacion(rs.getInt("id_asignacion"));
        at.setIdEmpleado(rs.getInt("id_empleado"));
        at.setIdTurno(rs.getInt("id_turno"));
        at.setFechaInicio(rs.getString("fecha_inicio"));
        at.setFechaFin(rs.getString("fecha_fin"));
        at.setEstado(rs.getString("estado"));
        at.setIdAdminAsigno(rs.getInt("id_admin_asigno"));
        at.setFechaRegistro(rs.getString("fecha_registro"));
        at.setNombreEmpleado(rs.getString("nombre_completo"));
        at.setNombreTurno(rs.getString("nombre_turno"));
        at.setNombreAdmin(rs.getString("nombre_admin"));
        return at;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
