package controlTurnos.dao;

import controlTurnos.modelo.Turno;
import controlTurnos.util.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TurnoDAO {

    public List<Turno> listarActivos() {
        List<Turno> lista = new ArrayList<>();
        String sql = "SELECT id_turno, nombre_turno, hora_inicio, hora_fin, horas_duracion, activo "
                   + "FROM turnos WHERE activo = 1 ORDER BY nombre_turno";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Turno t = new Turno();
                t.setIdTurno(rs.getInt("id_turno"));
                t.setNombreTurno(rs.getString("nombre_turno"));
                t.setHoraInicio(rs.getString("hora_inicio"));
                t.setHoraFin(rs.getString("hora_fin"));
                t.setHorasDuracion(rs.getInt("horas_duracion"));
                t.setActivo(rs.getInt("activo"));
                lista.add(t);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (ps != null) ps.close(); } catch (SQLException e) {}
            Conexion.cerrarConexion(con);
        }
        return lista;
    }
}
