package controlTurnos.dao;

import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BitacoraDetalleDAO {

    // ─────────────────────────────────────────────────────────
    // REGISTRAR DETALLE DE BITÁCORA
    // Si es creación: valorAnterior = null
    // Si es modificación: ambos tienen valor
    // Si es el usuario nuevo: idUsuarioAfectado puede ser null
    // ─────────────────────────────────────────────────────────
    public void registrar(long idLog, Integer idUsuarioAfectado,
                          String campocambiado,
                          String valorAnterior, String valorNuevo) {
        String sql = "INSERT INTO bitacora_detalle "
                   + "(id_log, id_usuario_afectado, campo_cambiado, valor_anterior, valor_nuevo) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setLong(1, idLog);
            if (idUsuarioAfectado != null) {
                ps.setInt(2, idUsuarioAfectado);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, campocambiado);
            if (valorAnterior != null) {
                ps.setString(4, valorAnterior);
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }
            ps.setString(5, valorNuevo);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al registrar bitacora_detalle: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
    }
}
