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
                // Lista todos los empleados para seleccionar
                request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                request.getRequestDispatcher("/jsp/reasignacion_rrhh.jsp")
                       .forward(request, response);
                break;

            case "formularioReasignar":
                // Cargar formulario con datos del empleado seleccionado
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

    // ─────────────────────────────────────────────────────────
    // REASIGNAR EMPLEADO — RRHH cambia área, turno y AdminArea
    // directamente sin necesidad de solicitud previa.
    // Registra en bitácora detalle qué cambió exactamente.
    // ─────────────────────────────────────────────────────────
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

        // Aplicar los tres cambios en BD
        boolean ok = empleadoDAO.reasignar(idEmpleado, idAreaNueva,
                idTurnoNuevo, idAdminNuevo, sesion.getIdEmpleado());

        if (ok) {
            // Bitácora principal
            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Reasignacion", "Crear",
                    "Empleado reasignado: " + antes.getNombreCompleto());

            // Bitácora detalle — solo registra los campos que cambiaron
            if (idLog > 0) {
                if (antes.getIdArea() != idAreaNueva) {
                    detalleDAO.registrar(idLog, idEmpleado, "id_area",
                            String.valueOf(antes.getIdArea()),
                            String.valueOf(idAreaNueva));
                }
                if (antes.getIdTurnoDefault() != idTurnoNuevo) {
                    detalleDAO.registrar(idLog, idEmpleado, "id_turno_default",
                            String.valueOf(antes.getIdTurnoDefault()),
                            String.valueOf(idTurnoNuevo));
                }
                if (antes.getIdAdminArea() != idAdminNuevo) {
                    detalleDAO.registrar(idLog, idEmpleado, "id_admin_area",
                            String.valueOf(antes.getIdAdminArea()),
                            String.valueOf(idAdminNuevo));
                }
            }
            request.setAttribute("exito",
                    "Empleado reasignado correctamente.");
        } else {
            request.setAttribute("error",
                    "Error al reasignar el empleado. Intente de nuevo.");
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
