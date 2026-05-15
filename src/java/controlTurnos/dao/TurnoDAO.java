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
        String sql = "SELECT id_turno, nombre_turno, hora_inicio, hora_fin, "
                   + "horas_duracion, activo FROM turnos WHERE activo = 1 ORDER BY nombre_turno";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.out.println("Error al listar turnos: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return lista;
    }

    public Turno buscarPorId(int idTurno) {
        Turno turno = null;
        String sql = "SELECT id_turno, nombre_turno, hora_inicio, hora_fin, "
                   + "horas_duracion, activo FROM turnos WHERE id_turno = ?";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setInt(1, idTurno);
            rs = ps.executeQuery();
            if (rs.next()) turno = mapear(rs);
        } catch (SQLException e) {
            System.out.println("Error al buscar turno: " + e.getMessage());
        } finally {
            cerrar(rs, ps, con);
        }
        return turno;
    }

    private Turno mapear(ResultSet rs) throws SQLException {
        Turno t = new Turno();
        t.setIdTurno(rs.getInt("id_turno"));
        t.setNombreTurno(rs.getString("nombre_turno"));
        t.setHoraInicio(rs.getString("hora_inicio"));
        t.setHoraFin(rs.getString("hora_fin"));
        t.setHorasDuracion(rs.getInt("horas_duracion"));
        t.setActivo(rs.getInt("activo"));
        return t;
    }

    private void cerrar(ResultSet rs, PreparedStatement ps, Connection con) {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (ps != null) ps.close(); } catch (SQLException e) {}
        Conexion.cerrarConexion(con);
    }
}
