package controlTurnos.dao;

import controlTurnos.modelo.Empleado;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDAO {

    // ─────────────────────────────────────────────────────────
    // LOGIN — todos los CU paso 1-2
    // ─────────────────────────────────────────────────────────
    public Empleado login(String usuario, String contrasena) {
        Empleado empleado = null;
        String sql = "SELECT e.id_empleado, e.dpi, e.nombre_completo, e.usuario, "
                   + "e.correo, e.estado, e.id_area, e.id_rol, e.id_turno_default, "
                   + "e.dias_vacaciones, r.nombre_rol, a.nombre_area, t.nombre_turno "
                   + "FROM empleados e "
                   + "INNER JOIN roles r ON e.id_rol = r.id_rol "
                   + "INNER JOIN areas a ON e.id_area = a.id_area "
                   + "LEFT JOIN turnos t ON e.id_turno_default = t.id_turno "
                   + "WHERE e.usuario = ? AND e.contrasena = ? AND e.estado = 'Activo'";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            rs = ps.executeQuery();
            if (rs.next()) {
                empleado = mapear(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    // ─────────────────────────────────────────────────────────
    // AGREGAR — CU1 pasos 8-19
    // ─────────────────────────────────────────────────────────
    public boolean agregar(Empleado emp) {
        String sql = "INSERT INTO empleados (dpi, nombre_completo, usuario, contrasena, "
                   + "correo, id_area, id_rol, id_turno_default, estado) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Activo')";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, emp.getDpi());
            ps.setString(2, emp.getNombreCompleto());
            ps.setString(3, emp.getUsuario());
            ps.setString(4, emp.getContrasena());
            ps.setString(5, emp.getCorreo());
            ps.setInt(6, emp.getIdArea());
            ps.setInt(7, emp.getIdRol());
            ps.setInt(8, emp.getIdTurnoDefault());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar empleado: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR TODOS — CU1-FA02
    // ─────────────────────────────────────────────────────────
    public List<Empleado> listarTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.dpi, e.nombre_completo, e.usuario, "
                   + "e.correo, e.estado, e.id_area, e.id_rol, e.id_turno_default, "
                   + "e.dias_vacaciones, r.nombre_rol, a.nombre_area, t.nombre_turno "
                   + "FROM empleados e "
                   + "INNER JOIN roles r ON e.id_rol = r.id_rol "
                   + "INNER JOIN areas a ON e.id_area = a.id_area "
                   + "LEFT JOIN turnos t ON e.id_turno_default = t.id_turno "
                   + "ORDER BY e.nombre_completo ASC";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // BUSCAR POR ID
    // ─────────────────────────────────────────────────────────
    public Empleado buscarPorId(int idEmpleado) {
        Empleado empleado = null;
        String sql = "SELECT e.id_empleado, e.dpi, e.nombre_completo, e.usuario, "
                   + "e.correo, e.estado, e.id_area, e.id_rol, e.id_turno_default, "
                   + "e.dias_vacaciones, r.nombre_rol, a.nombre_area, t.nombre_turno "
                   + "FROM empleados e "
                   + "INNER JOIN roles r ON e.id_rol = r.id_rol "
                   + "INNER JOIN areas a ON e.id_area = a.id_area "
                   + "LEFT JOIN turnos t ON e.id_turno_default = t.id_turno "
                   + "WHERE e.id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            if (rs.next()) {
                empleado = mapear(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar empleado: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    // ─────────────────────────────────────────────────────────
    // INACTIVAR — CU1-FA03
    // ─────────────────────────────────────────────────────────
    public boolean inactivar(int idEmpleado, String motivo) {
        String sql = "UPDATE empleados SET estado = 'Inactivo', motivo_inactivacion = ? "
                   + "WHERE id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, motivo);
            ps.setInt(2, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al inactivar empleado: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // LISTAR POR ÁREA Y TURNO — CU3 formulario asignar
    // ─────────────────────────────────────────────────────────
    public List<Empleado> listarPorAreaYTurno(int idArea, int idTurno) {
        List<Empleado> lista = new ArrayList<>();
        String sql = "SELECT e.id_empleado, e.dpi, e.nombre_completo, e.usuario, "
                   + "e.correo, e.estado, e.id_area, e.id_rol, e.id_turno_default, "
                   + "e.dias_vacaciones, r.nombre_rol, a.nombre_area, t.nombre_turno "
                   + "FROM empleados e "
                   + "INNER JOIN roles r ON e.id_rol = r.id_rol "
                   + "INNER JOIN areas a ON e.id_area = a.id_area "
                   + "LEFT JOIN turnos t ON e.id_turno_default = t.id_turno "
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? AND e.estado = 'Activo' "
                   + "ORDER BY e.nombre_completo ASC";
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
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al listar empleados por área y turno: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // ─────────────────────────────────────────────────────────
    // VERIFICAR USUARIO DUPLICADO — CU1 paso 18
    // ─────────────────────────────────────────────────────────
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT id_empleado FROM empleados WHERE usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
            return false;
        } finally {
            cerrar(rs, ps, con);
        }
    }

    // ─────────────────────────────────────────────────────────
    // MAPEAR ResultSet → Empleado
    // ─────────────────────────────────────────────────────────
    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setIdEmpleado(rs.getInt("id_empleado"));
        e.setDpi(rs.getString("dpi"));
        e.setNombreCompleto(rs.getString("nombre_completo"));
        e.setUsuario(rs.getString("usuario"));
        e.setCorreo(rs.getString("correo"));
        e.setEstado(rs.getString("estado"));
        e.setIdArea(rs.getInt("id_area"));
        e.setIdRol(rs.getInt("id_rol"));
        e.setIdTurnoDefault(rs.getInt("id_turno_default"));
        e.setNombreRol(rs.getString("nombre_rol"));
        e.setNombreArea(rs.getString("nombre_area"));
        e.setNombreTurno(rs.getString("nombre_turno"));
        return e;
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
