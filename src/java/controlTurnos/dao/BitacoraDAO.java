package controlTurnos.dao;

import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BitacoraDAO {

    // ─────────────────────────────────────────────────────────
    // REGISTRAR EN BITÁCORA
    // Fuente: CU1-AN07, CU2-AN01, CU4-AN03
    // tipo_operacion ENUM: Login, Crear, Aprobar, Rechazar,
    //                      Inactivar, Marcaje, Solicitar
    // ─────────────────────────────────────────────────────────
    public void registrar(int idEmpleado, String login, String modulo,
                          String tipoOperacion, String descripcion) {
        String sql = "INSERT INTO bitacora (id_empleado, login, modulo, tipo_operacion, descripcion) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idEmpleado);
            ps.setString(2, login);
            ps.setString(3, modulo);
            ps.setString(4, tipoOperacion);
            ps.setString(5, descripcion);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar bitacora: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
    }
}
