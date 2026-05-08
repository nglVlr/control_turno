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

    // SQL base con todos los JOINs necesarios
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
      + "INNER JOIN areas    a   ON e.id_area          = a.id_area "
      + "LEFT  JOIN turnos   t   ON e.id_turno_default = t.id_turno "
      + "LEFT  JOIN empleados adm ON e.id_admin_area   = adm.id_empleado "
      + "LEFT  JOIN empleados cre ON e.creado_por      = cre.id_empleado "
      + "LEFT  JOIN empleados mdf ON e.modificado_por  = mdf.id_empleado ";

    // ─────────────────────────────────────────────────────────
    // LOGIN — compara con MD5() de MySQL
    // La contraseña en BD está encriptada con MD5
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // LISTAR TODOS — AdminRRHH ve todos
    // ─────────────────────────────────────────────────────────
    public List<Empleado> listarTodos() {
        List<Empleado> lista = new ArrayList<>();
        String sql = SQL_BASE + "ORDER BY a.nombre_area, t.nombre_turno, e.nombre_completo";
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

    // ─────────────────────────────────────────────────────────
    // LISTAR POR ADMINAREA — AdminArea ve SOLO sus empleados
    // Nuevo requerimiento: cada empleado pertenece a un AdminArea
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // LISTAR POR AREA Y TURNO — para formulario de asignación
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // LISTAR ADMINAREA POR AREA Y TURNO
    // Para el combo al crear/editar empleado
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // AGREGAR — CU1 paso 7
    // Guarda id_admin_area y creado_por
    // Contraseña se guarda con MD5() de MySQL
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // BUSCAR POR ID
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // INACTIVAR — CU1-FA03
    // Guarda modificado_por
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // CAMBIAR ROL — FA09
    // Guarda modificado_por
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // CAMBIAR ADMIN AREA — cuando cambia turno o puesto
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // REASIGNAR — RRHH cambia área, turno y AdminArea directamente
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // EXISTEUSARIO — validación duplicado
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
    // BUSCAR ADMINAREA POR AREA Y TURNO — FA09 unicidad
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // NUEVOS POR ADMINAREA — Bug fix para LoginServlet
    // Usa id_admin_area en lugar de área+turno
    // ─────────────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────────────
    // LISTAR NUEVOS ÚLTIMAS 24H — por área y turno (método original)
    // ─────────────────────────────────────────────────────────
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
