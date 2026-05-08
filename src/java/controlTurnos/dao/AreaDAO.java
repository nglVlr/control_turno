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

    // Listar todas las áreas activas — para combos
    public List<Area> listarActivas() {
        List<Area> lista = new ArrayList<>();
        String sql = "SELECT id_area, nombre_area, descripcion, activo "
                   + "FROM areas WHERE activo = 1 ORDER BY nombre_area";
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
            System.out.println("Error al listar areas: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    // Buscar área por ID — para editar
    public Area buscarPorId(int idArea) {
        Area area = null;
        String sql = "SELECT id_area, nombre_area, descripcion, activo FROM areas WHERE id_area = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idArea);
            rs = ps.executeQuery();
            if (rs.next()) area = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al buscar area: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return area;
    }

    // Editar descripción del área — CU1 nuevo requerimiento
    public boolean editar(int idArea, String descripcion) {
        String sql = "UPDATE areas SET descripcion = ? WHERE id_area = ?";
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, descripcion);
            ps.setInt(2, idArea);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al editar area: " + e.getMessage());
            return false;
        } finally {
            cerrar(null, ps, con);
        }
    }

    private Area mapear(ResultSet rs) throws SQLException {
        Area a = new Area();
        a.setIdArea(rs.getInt("id_area"));
        a.setNombreArea(rs.getString("nombre_area"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setActivo(rs.getInt("activo"));
        return a;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
