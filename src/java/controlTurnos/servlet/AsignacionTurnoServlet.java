package controlTurnos.servlet;

import controlTurnos.dao.AsignacionTurnoDAO;
import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.AsignacionTurno;
import controlTurnos.modelo.Empleado;
import controlTurnos.modelo.Turno;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AsignacionTurnoServlet extends HttpServlet {

    private final AsignacionTurnoDAO asignacionDAO = new AsignacionTurnoDAO();
    private final EmpleadoDAO        empleadoDAO   = new EmpleadoDAO();
    private final TurnoDAO           turnoDAO      = new TurnoDAO();
    private final BitacoraDAO        bitacoraDAO   = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {

            case "listar":
                cargarListaYForward(request, response, sesion);
                break;

            case "formularioAsignar":
                // AdminArea solo ve SUS empleados (id_admin_area)
                // El turno asignado SIEMPRE es el turno del AdminArea — no se permite asignar otro
                if ("AdminArea".equals(sesion.getNombreRol())) {
                    // Solo sus empleados asignados
                    request.setAttribute("listaEmpleados",
                            empleadoDAO.listarPorAdminArea(sesion.getIdEmpleado()));
                    // Solo su propio turno — no puede asignar otro
                    Turno turnoAdmin = turnoDAO.buscarPorId(sesion.getIdTurnoDefault());
                    request.setAttribute("turnoAdmin", turnoAdmin);
                } else {
                    // AdminRRHH ve todos y puede elegir cualquier turno
                    request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                    request.setAttribute("listaTurnos", turnoDAO.listarActivos());
                }
                request.getRequestDispatcher("/jsp/asignar_turno.jsp")
                       .forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath()
                        + "/AsignacionTurnoServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) {
            response.sendRedirect(request.getContextPath()
                    + "/AsignacionTurnoServlet?accion=listar");
            return;
        }

        if ("asignar".equals(accion)) {
            asignarTurno(request, response, sesion);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/AsignacionTurnoServlet?accion=listar");
        }
    }

    // Reglas:
    // 1. El empleado debe pertenecer al AdminArea que asigna
    // 2. El turno asignado DEBE ser el mismo turno del AdminArea
    // 3. AdminRRHH puede asignar cualquier turno a cualquier empleado
    private void asignarTurno(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int    idEmpleado  = Integer.parseInt(request.getParameter("idEmpleado"));
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin    = request.getParameter("fechaFin");

        // Si es RRHH viene del form, si es AdminArea se fuerza el suyo
        int idTurno;
        if ("AdminArea".equals(sesion.getNombreRol())) {
            idTurno = sesion.getIdTurnoDefault();
        } else {
            idTurno = Integer.parseInt(request.getParameter("idTurno"));
        }

        if ("AdminArea".equals(sesion.getNombreRol())) {
            Empleado empTarget = empleadoDAO.buscarPorId(idEmpleado);
            if (empTarget == null
                    || empTarget.getIdAdminArea() != sesion.getIdEmpleado()) {
                request.setAttribute("error",
                        "No tienes permiso para asignar turnos a ese empleado.");
                cargarFormularioYForward(request, response, sesion);
                return;
            }
        }

        AsignacionTurno at = new AsignacionTurno();
        at.setIdEmpleado(idEmpleado);
        at.setIdTurno(idTurno);
        at.setFechaInicio(fechaInicio);
        at.setFechaFin(fechaFin);
        at.setIdAdminAsigno(sesion.getIdEmpleado());

        boolean exito = asignacionDAO.asignar(at);

        if (exito) {
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Asignacion Turnos", "Asignar",
                    "Turno id:" + idTurno + " asignado a empleado id:" + idEmpleado
                    + " | " + fechaInicio + " al " + fechaFin);
            request.setAttribute("exito", "Asignación creada con éxito.");
        } else {
            request.setAttribute("error", "Error al crear la asignación. Intente de nuevo.");
        }

        cargarListaYForward(request, response, sesion);
    }

    private void cargarListaYForward(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        if ("AdminRRHH".equals(sesion.getNombreRol())) {
            // RRHH ve todas las asignaciones del sistema
            request.setAttribute("listaAsignaciones", asignacionDAO.listarTodos());
        } else {
            // AdminArea ve solo las asignaciones de SUS empleados
            request.setAttribute("listaAsignaciones",
                    asignacionDAO.listarPorAdminArea(sesion.getIdEmpleado()));
        }
        request.getRequestDispatcher("/jsp/consultar_asignacion.jsp")
               .forward(request, response);
    }

    private void cargarFormularioYForward(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {
        if ("AdminArea".equals(sesion.getNombreRol())) {
            request.setAttribute("listaEmpleados",
                    empleadoDAO.listarPorAdminArea(sesion.getIdEmpleado()));
            request.setAttribute("turnoAdmin",
                    turnoDAO.buscarPorId(sesion.getIdTurnoDefault()));
        } else {
            request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
            request.setAttribute("listaTurnos", turnoDAO.listarActivos());
        }
        request.getRequestDispatcher("/jsp/asignar_turno.jsp")
               .forward(request, response);
    }

    private Empleado validarSesion(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("empleado") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        Empleado sesion = (Empleado) session.getAttribute("empleado");
        String rol = sesion.getNombreRol();
        if (!rol.equals("AdminArea") && !rol.equals("AdminRRHH")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
