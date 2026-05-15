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

    private static final String SQL_BASE =
        "SELECT e.id_empleado, e.dpi, e.nombre_completo, e.usuario, "
      + "e.correo, e.estado, e.id_area, e.id_rol, e.id_turno_default, "
      + "e.id_admin_area, e.dias_vacaciones, e.fecha_creacion, "
      + "e.creado_por, e.modificado_por, "
      + "r.nombre_rol, a.nombre_area, t.nombre_turno, "
      + "adm.nombre_completo AS nombre_admin_area, "
      + "cre.nombre_completo AS creado_por_nombre, "
      + "mdf.nombre_completo AS modificado_por_nombre "
      + "FROM empleados e "
      + "INNER JOIN roles    r   ON e.id_rol           = r.id_rol "
      + "LEFT  JOIN areas    a   ON e.id_area          = a.id_area "
      + "LEFT  JOIN turnos   t   ON e.id_turno_default = t.id_turno "
      + "LEFT  JOIN empleados adm ON e.id_admin_area   = adm.id_empleado "
      + "LEFT  JOIN empleados cre ON e.creado_por      = cre.id_empleado "
      + "LEFT  JOIN empleados mdf ON e.modificado_por  = mdf.id_empleado ";

    // La contraseña en BD está encriptada con MD5 — se compara con MD5() de MySQL
    public Empleado login(String usuario, String contrasena) {
        Empleado empleado = null;
        String sql = SQL_BASE
                   + "WHERE e.usuario = ? AND e.contrasena = MD5(?) AND e.estado = 'Activo'";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            rs = ps.executeQuery();
            if (rs.next()) empleado = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error en login: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    public List<Empleado> listarTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY IFNULL(a.nombre_area,''), IFNULL(t.nombre_turno,''), e.nombre_completo";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public List<Empleado> listarPorAdminArea(int idAdminArea) {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE
                   + "WHERE e.id_admin_area = ? AND e.estado = 'Activo' "
                   + "ORDER BY e.nombre_completo";
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
            System.out.println("Error al listar por adminArea: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public List<Empleado> listarPorAreaYTurno(int idArea, int idTurno) {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? AND e.estado = 'Activo' "
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
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar por area y turno: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public List<Empleado> listarAdminAreaPorAreaYTurno(int idArea, int idTurno) {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? "
                   + "AND e.id_rol = 2 AND e.estado = 'Activo' "
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
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar adminArea: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // AdminRRHH: área y turno pueden ser 0 (NULL en BD)
    // AdminArea: área y turno requeridos, sin AdminArea
    // Empleado : área, turno y AdminArea requeridos
    public boolean agregarConRol(Empleado emp) {
        String sql = "INSERT INTO empleados "
                   + "(dpi, nombre_completo, usuario, contrasena, correo, "
                   + "id_area, id_rol, id_turno_default, id_admin_area, estado, creado_por) "
                   + "VALUES (?, ?, ?, MD5(?), ?, ?, ?, ?, ?, 'Activo', ?)";
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
            // Área — NULL si es 0
            if (emp.getIdArea() > 0) ps.setInt(6, emp.getIdArea());
            else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setInt(7, emp.getIdRol());
            // Turno — NULL si es 0
            if (emp.getIdTurnoDefault() > 0) ps.setInt(8, emp.getIdTurnoDefault());
            else ps.setNull(8, java.sql.Types.INTEGER);
            // AdminArea — NULL si es 0 (AdminRRHH y AdminArea no dependen de nadie)
            if (emp.getIdAdminArea() > 0) ps.setInt(9, emp.getIdAdminArea());
            else ps.setNull(9, java.sql.Types.INTEGER);
            ps.setInt(10, emp.getCreadoPor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public boolean agregar(Empleado emp) {
        String sql = "INSERT INTO empleados "
                   + "(dpi, nombre_completo, usuario, contrasena, correo, "
                   + "id_area, id_rol, id_turno_default, id_admin_area, estado, creado_por) "
                   + "VALUES (?, ?, ?, MD5(?), ?, ?, 3, ?, ?, 'Activo', ?)";
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
            ps.setInt(7, emp.getIdTurnoDefault());
            ps.setInt(8, emp.getIdAdminArea());
            ps.setInt(9, emp.getCreadoPor());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al agregar empleado: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public Empleado buscarPorUsuario(String usuario) {
        Empleado empleado = null;
        String sql = SQL_BASE + "WHERE e.usuario = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            rs = ps.executeQuery();
            if (rs.next()) empleado = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al buscar por usuario: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    public Empleado buscarPorId(int idEmpleado) {
        Empleado empleado = null;
        String sql = SQL_BASE + "WHERE e.id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            rs = ps.executeQuery();
            if (rs.next()) empleado = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al buscar empleado: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    public boolean inactivar(int idEmpleado, String motivo, int modificadoPor) {
        String sql = "UPDATE empleados SET estado = 'Inactivo', "
                   + "motivo_inactivacion = ?, modificado_por = ? "
                   + "WHERE id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, motivo);
            ps.setInt(2, modificadoPor);
            ps.setInt(3, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al inactivar: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public boolean cambiarRol(int idEmpleado, int idRolNuevo, int modificadoPor) {
        String sql = "UPDATE empleados SET id_rol = ?, modificado_por = ? "
                   + "WHERE id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idRolNuevo);
            ps.setInt(2, modificadoPor);
            ps.setInt(3, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar rol: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public boolean cambiarAdminArea(int idEmpleado, int idAdminAreaNuevo, int modificadoPor) {
        String sql = "UPDATE empleados SET id_admin_area = ?, modificado_por = ? "
                   + "WHERE id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idAdminAreaNuevo);
            ps.setInt(2, modificadoPor);
            ps.setInt(3, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar adminArea: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public boolean reasignar(int idEmpleado, int idArea, int idTurno,
                             int idAdminArea, int modificadoPor) {
        String sql = "UPDATE empleados SET id_area = ?, id_turno_default = ?, "
                   + "id_admin_area = ?, modificado_por = ? "
                   + "WHERE id_empleado = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            ps.setInt(2, idTurno);
            ps.setInt(3, idAdminArea);
            ps.setInt(4, modificadoPor);
            ps.setInt(5, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al reasignar empleado: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    public boolean reactivar(int idEmpleado, int modificadoPor) {
        String sql = "UPDATE empleados SET estado = 'Activo', "
                   + "motivo_inactivacion = NULL, modificado_por = ? "
                   + "WHERE id_empleado = ? AND estado = 'Inactivo'";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, modificadoPor);
            ps.setInt(2, idEmpleado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al reactivar: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

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

    public Empleado buscarAdminAreaPorAreaYTurno(int idArea, int idTurno) {
        Empleado empleado = null;
        String sql = SQL_BASE
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? "
                   + "AND e.id_rol = 2 AND e.estado = 'Activo'";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            ps.setInt(2, idTurno);
            rs = ps.executeQuery();
            if (rs.next()) empleado = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al buscar adminArea: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return empleado;
    }

    // Usa id_admin_area en lugar de área+turno para encontrar empleados nuevos del AdminArea
    public List<Empleado> listarNuevosUltimas24HorasPorAdmin(int idAdminArea) {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE
                   + "WHERE e.id_admin_area = ? "
                   + "AND e.fecha_creacion >= NOW() - INTERVAL 24 HOUR "
                   + "ORDER BY e.fecha_creacion DESC";
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
            System.out.println("Error al listar nuevos por admin: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public List<Empleado> listarNuevosUltimas24Horas(int idArea, int idTurno) {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE
                   + "WHERE e.id_area = ? AND e.id_turno_default = ? "
                   + "AND e.fecha_creacion >= NOW() - INTERVAL 24 HOUR "
                   + "ORDER BY e.fecha_creacion DESC";
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
            System.out.println("Error al listar nuevos: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

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
        e.setIdAdminArea(rs.getInt("id_admin_area"));
        e.setDiasVacaciones(rs.getInt("dias_vacaciones"));
        e.setCreadoPor(rs.getInt("creado_por"));
        e.setModificadoPor(rs.getInt("modificado_por"));
        e.setNombreRol(rs.getString("nombre_rol"));
        e.setNombreArea(rs.getString("nombre_area"));
        e.setNombreTurno(rs.getString("nombre_turno"));
        e.setNombreAdminArea(rs.getString("nombre_admin_area"));
        e.setCreadoPorNombre(rs.getString("creado_por_nombre"));
        e.setModificadoPorNombre(rs.getString("modificado_por_nombre"));
        return e;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
