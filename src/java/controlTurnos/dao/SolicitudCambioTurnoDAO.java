package controlTurnos.dao;

import controlTurnos.modelo.SolicitudCambioTurno;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SolicitudCambioTurnoDAO {

    // ─────────────────────────────────────────────────────────
    // CREAR SOLICITUD — CU5-FA02
    // Empleado envía solicitud de cambio de turno/área
    // Estado inicial: Pendiente — lo resuelve el RRHH
    // ─────────────────────────────────────────────────────────
    public boolean crear(SolicitudCambioTurno s) {
        String sql = "INSERT INTO solicitudes_cambio_turno "
                   + "(id_empleado, fecha_inicial, id_turno_inicial, id_area_origen, "
                   + "fecha_nueva, id_turno_nuevo, id_area_destino, justificacion, estado, "
                   + "notif_admin_origen, notif_admin_destino) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Pendiente', 'Pendiente', 'Pendiente')";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, s.getIdEmpleado());
            ps.setString(2, s.getFechaInicial());
            ps.setInt(3, s.getIdTurnoInicial());
            ps.setInt(4, s.getIdAreaOrigen());
            ps.setString(5, s.getFechaNueva());
            ps.setInt(6, s.getIdTurnoNuevo());
            ps.setInt(7, s.getIdAreaDestino());
            ps.setString(8, s.getJustificacion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al crear solicitud cambio turno: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR PENDIENTES PARA RRHH — CU4
    // AdminRRHH ve todas las pendientes para decidir
    // ─────────────────────────────────────────────────────────
    public List<SolicitudCambioTurno> listarPendientesRRHH() {
        List<SolicitudCambioTurno> lista = new ArrayList<>();
        String sql = "SELECT s.*, e.nombre_completo AS nombre_empleado, "
                   + "ti.nombre_turno AS turno_inicial, tn.nombre_turno AS turno_nuevo, "
                   + "ao.nombre_area AS area_origen, ad.nombre_area AS area_destino "
                   + "FROM solicitudes_cambio_turno s "
                   + "INNER JOIN empleados e  ON s.id_empleado    = e.id_empleado "
                   + "INNER JOIN turnos ti    ON s.id_turno_inicial = ti.id_turno "
                   + "INNER JOIN turnos tn    ON s.id_turno_nuevo   = tn.id_turno "
                   + "INNER JOIN areas ao     ON s.id_area_origen   = ao.id_area "
                   + "INNER JOIN areas ad     ON s.id_area_destino  = ad.id_area "
                   + "WHERE s.estado = 'Pendiente' "
                   + "ORDER BY s.fecha_creacion ASC";
        return ejecutarLista(sql, null, null);
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR NOTIFICACIONES PARA ADMINAREA — CU4
    // AdminArea ve las solicitudes donde él es origen o destino
    // y que ya fueron resueltas por RRHH pero aún no vistas
    // ─────────────────────────────────────────────────────────
    public List<SolicitudCambioTurno> listarNotificacionesAdminArea(int idArea, int idTurno) {
        List<SolicitudCambioTurno> lista = new ArrayList<>();
        String sql = "SELECT s.*, e.nombre_completo AS nombre_empleado, "
                   + "ti.nombre_turno AS turno_inicial, tn.nombre_turno AS turno_nuevo, "
                   + "ao.nombre_area AS area_origen, ad.nombre_area AS area_destino "
                   + "FROM solicitudes_cambio_turno s "
                   + "INNER JOIN empleados e  ON s.id_empleado      = e.id_empleado "
                   + "INNER JOIN turnos ti    ON s.id_turno_inicial  = ti.id_turno "
                   + "INNER JOIN turnos tn    ON s.id_turno_nuevo    = tn.id_turno "
                   + "INNER JOIN areas ao     ON s.id_area_origen    = ao.id_area "
                   + "INNER JOIN areas ad     ON s.id_area_destino   = ad.id_area "
                   + "WHERE s.estado != 'Pendiente' "
                   + "AND ((s.id_area_origen = ? AND s.id_turno_inicial = ? AND s.notif_admin_origen = 'Pendiente') "
                   + "  OR (s.id_area_destino = ? AND s.id_turno_nuevo = ? AND s.notif_admin_destino = 'Pendiente')) "
                   + "ORDER BY s.fecha_resolucion DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            ps.setInt(2, idTurno);
            ps.setInt(3, idArea);
            ps.setInt(4, idTurno);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar notificaciones: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR TODAS — historial completo para AdminRRHH
    // ─────────────────────────────────────────────────────────
    public List<SolicitudCambioTurno> listarTodas() {
        String sql = "SELECT s.*, e.nombre_completo AS nombre_empleado, "
                   + "ti.nombre_turno AS turno_inicial, tn.nombre_turno AS turno_nuevo, "
                   + "ao.nombre_area AS area_origen, ad.nombre_area AS area_destino "
                   + "FROM solicitudes_cambio_turno s "
                   + "INNER JOIN empleados e  ON s.id_empleado      = e.id_empleado "
                   + "INNER JOIN turnos ti    ON s.id_turno_inicial  = ti.id_turno "
                   + "INNER JOIN turnos tn    ON s.id_turno_nuevo    = tn.id_turno "
                   + "INNER JOIN areas ao     ON s.id_area_origen    = ao.id_area "
                   + "INNER JOIN areas ad     ON s.id_area_destino   = ad.id_area "
                   + "ORDER BY s.fecha_creacion DESC";
        return ejecutarLista(sql, null, null);
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR POR EMPLEADO — historial del empleado
    // ─────────────────────────────────────────────────────────
    public List<SolicitudCambioTurno> listarPorEmpleado(int idEmpleado) {
        String sql = "SELECT s.*, e.nombre_completo AS nombre_empleado, "
                   + "ti.nombre_turno AS turno_inicial, tn.nombre_turno AS turno_nuevo, "
                   + "ao.nombre_area AS area_origen, ad.nombre_area AS area_destino "
                   + "FROM solicitudes_cambio_turno s "
                   + "INNER JOIN empleados e  ON s.id_empleado      = e.id_empleado "
                   + "INNER JOIN turnos ti    ON s.id_turno_inicial  = ti.id_turno "
                   + "INNER JOIN turnos tn    ON s.id_turno_nuevo    = tn.id_turno "
                   + "INNER JOIN areas ao     ON s.id_area_origen    = ao.id_area "
                   + "INNER JOIN areas ad     ON s.id_area_destino   = ad.id_area "
                   + "WHERE s.id_empleado = ? "
                   + "ORDER BY s.fecha_creacion DESC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SolicitudCambioTurno> lista = new ArrayList<>();
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
    // RESOLVER POR RRHH — CU4: Aprobar o Rechazar
    // Si aprueba: actualiza turno del empleado en tabla empleados
    // Notifica a AdminArea origen y destino (notif = Pendiente)
    // Si es misma área+turno: notif_destino = Visto (no aplica)
    // ─────────────────────────────────────────────────────────
    public boolean resolver(int idSolicitud, int idRrhh, String decision, String observacion) {
        String sql = "UPDATE solicitudes_cambio_turno "
                   + "SET estado = ?, id_rrhh_resolvio = ?, "
                   + "fecha_resolucion = NOW(), observacion_rrhh = ?, "
                   + "notif_admin_origen = 'Pendiente', notif_admin_destino = 'Pendiente' "
                   + "WHERE id_solicitud_ct = ? AND estado = 'Pendiente'";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, decision);  // 'Aprobado' o 'Rechazado'
            ps.setInt(2, idRrhh);
            ps.setString(3, observacion);
            ps.setInt(4, idSolicitud);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al resolver solicitud: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // APLICAR CAMBIO DE TURNO — solo si fue Aprobado
    // Actualiza el turno default del empleado en tabla empleados
    // ─────────────────────────────────────────────────────────
    public boolean aplicarCambio(int idSolicitud) {
        String sql = "UPDATE empleados e "
                   + "INNER JOIN solicitudes_cambio_turno s ON e.id_empleado = s.id_empleado "
                   + "SET e.id_turno_default = s.id_turno_nuevo, "
                   + "    e.id_area = s.id_area_destino "
                   + "WHERE s.id_solicitud_ct = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idSolicitud);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al aplicar cambio: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MARCAR NOTIFICACIÓN VISTA — AdminArea marca como vista
    // ─────────────────────────────────────────────────────────
    public void marcarVistaOrigen(int idSolicitud) {
        ejecutarUpdate("UPDATE solicitudes_cambio_turno SET notif_admin_origen = 'Visto' "
                + "WHERE id_solicitud_ct = ?", idSolicitud);
    }

    public void marcarVistaDestino(int idSolicitud) {
        ejecutarUpdate("UPDATE solicitudes_cambio_turno SET notif_admin_destino = 'Visto' "
                + "WHERE id_solicitud_ct = ?", idSolicitud);
    }

    // ─────────────────────────────────────────────────────────
    // CONTAR PENDIENTES RRHH — para badge en el menú
    // ─────────────────────────────────────────────────────────
    public int contarPendientesRRHH() {
        String sql = "SELECT COUNT(*) FROM solicitudes_cambio_turno WHERE estado = 'Pendiente'";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar pendientes: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // CONTAR NOTIFICACIONES ADMINAREA — para badge en menú
    // ─────────────────────────────────────────────────────────
    public int contarNotificacionesAdminArea(int idArea, int idTurno) {
        String sql = "SELECT COUNT(*) FROM solicitudes_cambio_turno "
                   + "WHERE estado != 'Pendiente' "
                   + "AND ((id_area_origen = ? AND id_turno_inicial = ? AND notif_admin_origen = 'Pendiente') "
                   + "  OR (id_area_destino = ? AND id_turno_nuevo = ? AND notif_admin_destino = 'Pendiente'))";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea); ps.setInt(2, idTurno);
            ps.setInt(3, idArea); ps.setInt(4, idTurno);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error al contar notificaciones: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return 0;
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private List<SolicitudCambioTurno> ejecutarLista(String sql, Integer p1, Integer p2) {
        List<SolicitudCambioTurno> lista = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            if (p1 != null) ps.setInt(1, p1);
            if (p2 != null) ps.setInt(2, p2);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al ejecutar lista: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    private void ejecutarUpdate(String sql, int param) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, param);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error en update: " + e.getMessage());
        } finally {
            cerrar(null, ps, con);
        }
    }

    private SolicitudCambioTurno mapear(ResultSet rs) throws SQLException {
        SolicitudCambioTurno s = new SolicitudCambioTurno();
        s.setIdSolicitudCt(rs.getInt("id_solicitud_ct"));
        s.setIdEmpleado(rs.getInt("id_empleado"));
        s.setFechaInicial(rs.getString("fecha_inicial"));
        s.setIdTurnoInicial(rs.getInt("id_turno_inicial"));
        s.setIdAreaOrigen(rs.getInt("id_area_origen"));
        s.setFechaNueva(rs.getString("fecha_nueva"));
        s.setIdTurnoNuevo(rs.getInt("id_turno_nuevo"));
        s.setIdAreaDestino(rs.getInt("id_area_destino"));
        s.setJustificacion(rs.getString("justificacion"));
        s.setEstado(rs.getString("estado"));
        s.setNotifAdminOrigen(rs.getString("notif_admin_origen"));
        s.setNotifAdminDestino(rs.getString("notif_admin_destino"));
        s.setObservacionRrhh(rs.getString("observacion_rrhh"));
        s.setFechaCreacion(rs.getString("fecha_creacion"));
        s.setNombreEmpleado(rs.getString("nombre_empleado"));
        s.setNombreTurnoInicial(rs.getString("turno_inicial"));
        s.setNombreTurnoNuevo(rs.getString("turno_nuevo"));
        s.setNombreAreaOrigen(rs.getString("area_origen"));
        s.setNombreAreaDestino(rs.getString("area_destino"));
        return s;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
