package controlTurnos.servlet;

import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.modelo.Empleado;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        EmpleadoDAO dao = new EmpleadoDAO();
        Empleado empleado = dao.login(usuario, contrasena);

        if (empleado != null) {
            HttpSession session = request.getSession();
            session.setAttribute("empleado", empleado);

            switch (empleado.getNombreRol()) {
                case "AdminRRHH":
                    response.sendRedirect("jsp/menu_admin_rrhh.jsp");
                    break;
                case "AdminArea":
                    response.sendRedirect("jsp/menu_admin_area.jsp");
                    break;
                case "Empleado":
                    response.sendRedirect("jsp/menu_empleado.jsp");
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
                    response.sendRedirect("jsp/menu_admin_rrhh.jsp");
                    break;
                case "AdminArea":
                    response.sendRedirect("jsp/menu_admin_area.jsp");
                    break;
                default:
                    response.sendRedirect("jsp/menu_empleado.jsp");
            }
        } else {
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
