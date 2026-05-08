package controlTurnos.dao;

import controlTurnos.modelo.Area;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AreaDAO {

    public List<Area> listarActivas() {
        List<Area> lista = new ArrayList<>();
        String sql = "SELECT id_area, nombre_area, descripcion, activo FROM areas WHERE activo = 1 ORDER BY nombre_area";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Area a = new Area();
                a.setIdArea(rs.getInt("id_area"));
                a.setNombreArea(rs.getString("nombre_area"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setActivo(rs.getInt("activo"));
                lista.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar areas: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
        return lista;
    }
}
