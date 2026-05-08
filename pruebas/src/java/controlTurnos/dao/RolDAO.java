package controlTurnos.dao;

import controlTurnos.modelo.Rol;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    public List<Rol> listarActivos() {
        List<Rol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol, descripcion, activo FROM roles WHERE activo = 1 ORDER BY nombre_rol";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Rol r = new Rol();
                r.setIdRol(rs.getInt("id_rol"));
                r.setNombreRol(rs.getString("nombre_rol"));
                r.setDescripcion(rs.getString("descripcion"));
                r.setActivo(rs.getInt("activo"));
                lista.add(r);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar roles: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
        return lista;
    }
}
