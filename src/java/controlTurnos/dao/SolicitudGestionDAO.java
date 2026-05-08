package controlTurnos.dao;

import controlTurnos.modelo.SolicitudGestion;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudGestionDAO {

    // ─────────────────────────────────────────────────────────
    // CREAR — CU5 paso 8
    // Empleado crea solicitud → estado: Pendiente
    // AdminArea la verá en su módulo de gestión
    // ─────────────────────────────────────────────────────────
    public boolean crear(SolicitudGestion s) {
        String sql = "INSERT INTO solicitudes_gestion "
                   + "(id_empleado, id_tipo_gestion, fecha_inicio, fecha_fin, motivo, estado) "
                   + "VALUES (?, ?, ?, ?, ?, 'Pendiente')";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, s.getIdEmpleado());
            ps.setInt(2, s.getIdTipoGestion());
            ps.setString(3, s.getFechaInicio());
            ps.setString(4, s.getFechaFin());
            ps.setString(5, s.getMotivo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear solicitud gestion: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR PENDIENTES POR ADMINAREA — Bug fix
    // Usa id_admin_area en lugar de área+turno
    // ─────────────────────────────────────────────────────────
    public List<SolicitudGestion> listarPendientesPorAdminArea(int idAdminArea) {
        List<SolicitudGestion> lista = new ArrayList<>();
        String sql = "SELECT sg.*, e.nombre_completo AS nombre_empleado, "
                   + "tg.nombre AS nombre_tipo, a.nombre_area "
                   + "FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e      ON sg.id_empleado     = e.id_empleado "
                   + "INNER JOIN tipos_gestion tg ON sg.id_tipo_gestion = tg.id_tipo_gestion "
                   + "INNER JOIN areas a           ON e.id_area          = a.id_area "
                   + "WHERE e.id_admin_area = ? AND sg.estado = 'Pendiente' "
                   + "ORDER BY sg.fecha_creacion ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idAdminArea);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar pendientes por adminArea: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public int contarPendientesPorAdminArea(int idAdminArea) {
        String sql = "SELECT COUNT(*) FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e ON sg.id_empleado = e.id_empleado "
                   + "WHERE e.id_admin_area = ? AND sg.estado = 'Pendiente'";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idAdminArea);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR PENDIENTES PARA ADMINAREA — método anterior por área+turno
    // AdminArea ve solicitudes Pendiente de empleados de su área+turno
    // ─────────────────────────────────────────────────────────
    public List<SolicitudGestion> listarPendientesPorAreaYTurno(int idArea, int idTurno) {
        List<SolicitudGestion> lista = new ArrayList<>();
        String sql = "SELECT sg.*, e.nombre_completo AS nombre_empleado, "
                   + "tg.nombre AS nombre_tipo, a.nombre_area "
                   + "FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e   ON sg.id_empleado     = e.id_empleado "
                   + "INNER JOIN tipos_gestion tg ON sg.id_tipo_gestion = tg.id_tipo_gestion "
                   + "INNER JOIN areas a        ON e.id_area          = a.id_area "
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? "
                   + "AND sg.estado = 'Pendiente' "
                   + "ORDER BY sg.fecha_creacion ASC";
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
            System.out.println("Error al listar pendientes: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR PENDIENTES PARA RRHH — CU1-FA06
    // RRHH ve solicitudes en estado 'Aprobada AdminArea' (pendiente RRHH)
    // ─────────────────────────────────────────────────────────
    public List<SolicitudGestion> listarPendientesRRHH() {
        List<SolicitudGestion> lista = new ArrayList<>();
        String sql = "SELECT sg.*, e.nombre_completo AS nombre_empleado, "
                   + "tg.nombre AS nombre_tipo, a.nombre_area "
                   + "FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e      ON sg.id_empleado     = e.id_empleado "
                   + "INNER JOIN tipos_gestion tg ON sg.id_tipo_gestion = tg.id_tipo_gestion "
                   + "INNER JOIN areas a           ON e.id_area          = a.id_area "
                   + "WHERE sg.estado = 'Aprobada AdminArea' "
                   + "ORDER BY sg.fecha_creacion ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar pendientes RRHH: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR POR EMPLEADO — historial del empleado CU5
    // ─────────────────────────────────────────────────────────
    public List<SolicitudGestion> listarPorEmpleado(int idEmpleado) {
        List<SolicitudGestion> lista = new ArrayList<>();
        String sql = "SELECT sg.*, e.nombre_completo AS nombre_empleado, "
                   + "tg.nombre AS nombre_tipo, a.nombre_area "
                   + "FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e      ON sg.id_empleado     = e.id_empleado "
                   + "INNER JOIN tipos_gestion tg ON sg.id_tipo_gestion = tg.id_tipo_gestion "
                   + "INNER JOIN areas a           ON e.id_area          = a.id_area "
                   + "WHERE sg.id_empleado = ? "
                   + "ORDER BY sg.fecha_creacion DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar por empleado: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // RESOLVER POR ADMINAREA — CU4-FA03/FA04
    // Aprobar → estado: 'Aprobada AdminArea' → pasa a RRHH
    // Rechazar → estado: 'Rechazada AdminArea'
    // ─────────────────────────────────────────────────────────
    public boolean resolverAdminArea(int idSolicitud, int idAdmin, String decision, String obs) {
        String estadoNuevo = "Aprobar".equals(decision)
                ? "Aprobada AdminArea" : "Rechazada AdminArea";
        String sql = "UPDATE solicitudes_gestion "
                   + "SET estado = ?, id_admin_resolvio = ?, "
                   + "fecha_resolucion = NOW(), observacion_admin = ? "
                   + "WHERE id_solicitud = ? AND estado = 'Pendiente'";
        return ejecutarUpdate(sql, estadoNuevo, idAdmin, obs, idSolicitud);
    }

    // ─────────────────────────────────────────────────────────
    // RESOLVER POR RRHH — CU1-FA06
    // Aprobar → estado: 'Aprobada RRHH'
    // Rechazar → estado: 'Rechazada RRHH'
    // ─────────────────────────────────────────────────────────
    public boolean resolverRRHH(int idSolicitud, int idAdmin, String decision, String obs) {
        String estadoNuevo = "Aprobar".equals(decision)
                ? "Aprobada RRHH" : "Rechazada RRHH";
        String sql = "UPDATE solicitudes_gestion "
                   + "SET estado = ?, id_admin_resolvio = ?, "
                   + "fecha_resolucion = NOW(), observacion_admin = ? "
                   + "WHERE id_solicitud = ? AND estado = 'Aprobada AdminArea'";
        return ejecutarUpdate(sql, estadoNuevo, idAdmin, obs, idSolicitud);
    }

    // ─────────────────────────────────────────────────────────
    // CONTAR PENDIENTES — para badges en los menús
    // ─────────────────────────────────────────────────────────
    public int contarPendientesPorAreaYTurno(int idArea, int idTurno) {
        String sql = "SELECT COUNT(*) FROM solicitudes_gestion sg "
                   + "INNER JOIN empleados e ON sg.id_empleado = e.id_empleado "
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? AND sg.estado = 'Pendiente'";
        return contarQuery(sql, idArea, idTurno);
    }

    public int contarPendientesRRHH() {
        String sql = "SELECT COUNT(*) FROM solicitudes_gestion WHERE estado = 'Aprobada AdminArea'";
        return contarQuerySinParams(sql);
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private boolean ejecutarUpdate(String sql, String estado, int idAdmin, String obs, int idSolicitud) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, idAdmin);
            ps.setString(3, obs);
            ps.setInt(4, idSolicitud);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al resolver: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    private int contarQuery(String sql, int p1, int p2) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, p1); ps.setInt(2, p2);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return 0;
    }

    private int contarQuerySinParams(String sql) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return 0;
    }

    private SolicitudGestion mapear(ResultSet rs) throws SQLException {
        SolicitudGestion s = new SolicitudGestion();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setIdEmpleado(rs.getInt("id_empleado"));
        s.setIdTipoGestion(rs.getInt("id_tipo_gestion"));
        s.setFechaInicio(rs.getString("fecha_inicio"));
        s.setFechaFin(rs.getString("fecha_fin"));
        s.setMotivo(rs.getString("motivo"));
        s.setEstado(rs.getString("estado"));
        s.setObservacionAdmin(rs.getString("observacion_admin"));
        s.setFechaCreacion(rs.getString("fecha_creacion"));
        s.setNombreEmpleado(rs.getString("nombre_empleado"));
        s.setNombreTipoGestion(rs.getString("nombre_tipo"));
        s.setNombreArea(rs.getString("nombre_area"));
        return s;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
