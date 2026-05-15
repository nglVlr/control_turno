package controlTurnos.servlet;

import controlTurnos.dao.AreaDAO;
import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.BitacoraDetalleDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ReasignacionServlet extends HttpServlet {

    private final EmpleadoDAO       empleadoDAO = new EmpleadoDAO();
    private final AreaDAO           areaDAO     = new AreaDAO();
    private final TurnoDAO          turnoDAO    = new TurnoDAO();
    private final BitacoraDAO       bitacoraDAO = new BitacoraDAO();
    private final BitacoraDetalleDAO detalleDAO = new BitacoraDetalleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesionRRHH(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {

            case "listar":
                request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                request.getRequestDispatcher("/jsp/reasignacion_rrhh.jsp")
                       .forward(request, response);
                break;

            case "formularioReasignar":
                int idEmp = Integer.parseInt(request.getParameter("id"));
                Empleado emp = empleadoDAO.buscarPorId(idEmp);
                if (emp == null) {
                    response.sendRedirect(request.getContextPath()
                            + "/ReasignacionServlet?accion=listar");
                    return;
                }
                request.setAttribute("empleado",    emp);
                request.setAttribute("listaAreas",  areaDAO.listarActivas());
                request.setAttribute("listaTurnos", turnoDAO.listarActivos());
                request.getRequestDispatcher("/jsp/formulario_reasignacion.jsp")
                       .forward(request, response);
                break;

            // AJAX — cargar AdminAreas disponibles según área+turno nuevo
            case "adminAreasPorAreaYTurno":
                int idArea  = Integer.parseInt(request.getParameter("idArea"));
                int idTurno = Integer.parseInt(request.getParameter("idTurno"));
                request.setAttribute("listaAdmins",
                        empleadoDAO.listarAdminAreaPorAreaYTurno(idArea, idTurno));
                request.getRequestDispatcher("/jsp/combo_admins.jsp")
                       .forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath()
                        + "/ReasignacionServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesionRRHH(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if ("reasignar".equals(accion)) {
            reasignarEmpleado(request, response, sesion);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/ReasignacionServlet?accion=listar");
        }
    }

    // RRHH cambia área, turno y AdminArea directamente sin necesidad de solicitud previa
    private void reasignarEmpleado(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int idEmpleado    = Integer.parseInt(request.getParameter("idEmpleado"));
        int idAreaNueva   = Integer.parseInt(request.getParameter("idArea"));
        int idTurnoNuevo  = Integer.parseInt(request.getParameter("idTurno"));
        int idAdminNuevo  = Integer.parseInt(request.getParameter("idAdminArea"));

        Empleado antes = empleadoDAO.buscarPorId(idEmpleado);
        if (antes == null) {
            request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
            request.setAttribute("error", "Empleado no encontrado.");
            request.getRequestDispatcher("/jsp/reasignacion_rrhh.jsp")
                   .forward(request, response);
            return;
        }

        boolean ok = empleadoDAO.reasignar(idEmpleado, idAreaNueva,
                idTurnoNuevo, idAdminNuevo, sesion.getIdEmpleado());

        if (ok) {
            // Buscar el empleado después del cambio para tener los nombres legibles
            Empleado despues = empleadoDAO.buscarPorId(idEmpleado);

            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Reasignacion", "Crear",
                    "Empleado reasignado: " + antes.getNombreCompleto());

            // Solo registra los campos que efectivamente cambiaron
            if (idLog > 0 && despues != null) {
                if (antes.getIdArea() != idAreaNueva) {
                    detalleDAO.registrar(idLog, idEmpleado, "area",
                            antes.getNombreArea(),
                            despues.getNombreArea());
                }
                if (antes.getIdTurnoDefault() != idTurnoNuevo) {
                    detalleDAO.registrar(idLog, idEmpleado, "turno",
                            antes.getNombreTurno() != null ? antes.getNombreTurno() : "Sin turno",
                            despues.getNombreTurno() != null ? despues.getNombreTurno() : "Sin turno");
                }
                if (antes.getIdAdminArea() != idAdminNuevo) {
                    detalleDAO.registrar(idLog, idEmpleado, "admin_area",
                            antes.getNombreAdminArea() != null ? antes.getNombreAdminArea() : "Sin asignar",
                            despues.getNombreAdminArea() != null ? despues.getNombreAdminArea() : "Sin asignar");
                }
            }
            request.setAttribute("exito", "Empleado reasignado correctamente.");
        } else {
            request.setAttribute("error", "Error al reasignar el empleado. Intente de nuevo.");
        }

        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        request.getRequestDispatcher("/jsp/reasignacion_rrhh.jsp")
               .forward(request, response);
    }

    private Empleado validarSesionRRHH(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("empleado") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        Empleado sesion = (Empleado) session.getAttribute("empleado");
        if (!"AdminRRHH".equals(sesion.getNombreRol())) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
