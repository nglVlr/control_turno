package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.modelo.Empleado;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final BitacoraDAO bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario   = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        Empleado empleado = empleadoDAO.login(usuario, contrasena);

        if (empleado != null) {
            HttpSession session = request.getSession();
            session.setAttribute("empleado", empleado);

            // Bitácora — registrar login exitoso
            bitacoraDAO.registrar(empleado.getIdEmpleado(), empleado.getUsuario(),
                    "Login", "Login", "Inicio de sesion: " + empleado.getNombreCompleto());

            switch (empleado.getNombreRol()) {
                case "AdminRRHH":
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_admin_rrhh.jsp");
                    break;
                case "AdminArea":
                    // Bug fix: cargar nuevos empleados asignados a este AdminArea
                    List<Empleado> nuevos = empleadoDAO.listarNuevosUltimas24HorasPorAdmin(
                            empleado.getIdEmpleado());
                    session.setAttribute("notificacionNuevos", nuevos);
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_admin_area.jsp");
                    break;
                case "Empleado":
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_empleado.jsp");
                    break;
                default:
                    request.setAttribute("error", "Rol no reconocido.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Credenciales incorrectas.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("empleado") != null) {
            Empleado empleado = (Empleado) session.getAttribute("empleado");
            switch (empleado.getNombreRol()) {
                case "AdminRRHH":
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_admin_rrhh.jsp");
                    break;
                case "AdminArea":
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_admin_area.jsp");
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/jsp/menu_empleado.jsp");
            }
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
