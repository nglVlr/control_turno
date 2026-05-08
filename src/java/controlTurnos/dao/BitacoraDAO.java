package controlTurnos.dao;

import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BitacoraDAO {

    // ─────────────────────────────────────────────────────────
    // REGISTRAR EN BITÁCORA — retorna el id_log generado
    // Se necesita el id_log para poder registrar el detalle
    // en BitacoraDetalleDAO
    // ─────────────────────────────────────────────────────────
    public long registrar(int idEmpleado, String login, String modulo,
                          String tipoOperacion, String descripcion) {
        String sql = "INSERT INTO bitacora "
                   + "(id_empleado, login, modulo, tipo_operacion, descripcion) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idEmpleado);
            ps.setString(2, login);
            ps.setString(3, modulo);
            ps.setString(4, tipoOperacion);
            ps.setString(5, descripcion);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            System.out.println("Error al registrar bitacora: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
        return -1;
    }
}
