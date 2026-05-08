package controlTurnos.servlet;

import controlTurnos.dao.AreaDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.RolDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EmpleadoServlet")
public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final AreaDAO     areaDAO     = new AreaDAO();
    private final TurnoDAO    turnoDAO    = new TurnoDAO();
    private final RolDAO      rolDAO      = new RolDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validarSesion(request, response)) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {
            case "listar":
                request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                request.getRequestDispatcher("/jsp/consultar_empleado.jsp").forward(request, response);
                break;
            case "formularioAgregar":
                cargarCombos(request);
                request.getRequestDispatcher("/jsp/agregar_empleado.jsp").forward(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validarSesion(request, response)) return;

        String accion = request.getParameter("accion");

        switch (accion) {
            case "agregar":
                agregarEmpleado(request, response);
                break;
            case "inactivar":
                inactivarEmpleado(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
        }
    }

    private void agregarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario").trim();

        // CU1 paso 18 — validar usuario duplicado
        if (empleadoDAO.existeUsuario(usuario)) {
            cargarCombos(request);
            request.setAttribute("error", "Error: El usuario ya existe en el sistema.");
            request.getRequestDispatcher("/jsp/agregar_empleado.jsp").forward(request, response);
            return;
        }

        Empleado emp = new Empleado();
        emp.setDpi(request.getParameter("dpi").trim());
        emp.setNombreCompleto(request.getParameter("nombreCompleto").trim());
        emp.setUsuario(usuario);
        emp.setContrasena(request.getParameter("contrasena").trim());
        emp.setCorreo(request.getParameter("correo").trim());
        emp.setIdArea(Integer.parseInt(request.getParameter("idArea")));
        emp.setIdRol(Integer.parseInt(request.getParameter("idRol")));
        emp.setIdTurnoDefault(Integer.parseInt(request.getParameter("idTurno")));

        boolean exito = empleadoDAO.agregar(emp);

        cargarCombos(request);
        if (exito) {
            request.setAttribute("exito", "Empleado creado correctamente.");
        } else {
            request.setAttribute("error", "Error: Ha ocurrido un error al registrar el empleado.");
        }
        request.getRequestDispatcher("/jsp/agregar_empleado.jsp").forward(request, response);
    }

    private void inactivarEmpleado(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idEmpleado = Integer.parseInt(request.getParameter("idEmpleado"));
        String motivo  = request.getParameter("motivoInactivacion");

        boolean exito = empleadoDAO.inactivar(idEmpleado, motivo);

        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        if (exito) {
            request.setAttribute("exito", "Empleado inactivado correctamente.");
        } else {
            request.setAttribute("error", "Error al inactivar el empleado.");
        }
        request.getRequestDispatcher("/jsp/consultar_empleado.jsp").forward(request, response);
    }

    private void cargarCombos(HttpServletRequest request) {
        request.setAttribute("listaAreas",  areaDAO.listarActivas());
        request.setAttribute("listaTurnos", turnoDAO.listarActivos());
        request.setAttribute("listaRoles",  rolDAO.listarActivos());
    }

    private boolean validarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("empleado") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return false;
        }
        Empleado sesion = (Empleado) session.getAttribute("empleado");
        if (!sesion.getNombreRol().equals("AdminRRHH")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return false;
        }
        return true;
    }
}
