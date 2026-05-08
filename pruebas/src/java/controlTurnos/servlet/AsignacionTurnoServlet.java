package controlTurnos.servlet;

import controlTurnos.dao.AsignacionTurnoDAO;
import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.AsignacionTurno;
import controlTurnos.modelo.Empleado;
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
                // CU3 paso 5 — mostrar formulario
                // AdminArea solo ve empleados de su área y turno
                request.setAttribute("listaTurnos", turnoDAO.listarActivos());
                request.setAttribute("listaEmpleados",
                        empleadoDAO.listarPorAreaYTurno(
                                sesion.getIdArea(), sesion.getIdTurnoDefault()));
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

    // ─────────────────────────────────────────────────────────
    // ASIGNAR TURNO — CU3 pasos 6-12
    // AdminArea solo puede asignar a empleados de su área y turno
    // RN02: solo turnos de 8 horas
    // ─────────────────────────────────────────────────────────
    private void asignarTurno(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int    idEmpleado  = Integer.parseInt(request.getParameter("idEmpleado"));
        int    idTurno     = Integer.parseInt(request.getParameter("idTurno"));
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin    = request.getParameter("fechaFin");

        // Seguridad: verificar que el empleado pertenece al área y turno del AdminArea
        Empleado empTarget = empleadoDAO.buscarPorId(idEmpleado);
        if (empTarget == null
                || empTarget.getIdArea() != sesion.getIdArea()
                || empTarget.getIdTurnoDefault() != sesion.getIdTurnoDefault()) {
            request.setAttribute("error",
                    "No tiene permiso para asignar turnos a ese empleado.");
            cargarFormularioYForward(request, response, sesion);
            return;
        }

        AsignacionTurno at = new AsignacionTurno();
        at.setIdEmpleado(idEmpleado);
        at.setIdTurno(idTurno);
        at.setFechaInicio(fechaInicio);
        at.setFechaFin(fechaFin);
        at.setIdAdminAsigno(sesion.getIdEmpleado());

        boolean exito = asignacionDAO.asignar(at);

        if (exito) {
            // CU3 paso 12 — bitácora
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Asignacion Turnos", "Asignar",
                    "Turno asignado a empleado id:" + idEmpleado
                    + " | Turno id:" + idTurno
                    + " | " + fechaInicio + " al " + fechaFin);
            request.setAttribute("exito", "Asignación creada con éxito");
        } else {
            request.setAttribute("error",
                    "Error al crear la asignación. Intente de nuevo.");
        }

        cargarListaYForward(request, response, sesion);
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private void cargarListaYForward(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        if ("AdminRRHH".equals(sesion.getNombreRol())) {
            request.setAttribute("listaAsignaciones", asignacionDAO.listarTodos());
        } else {
            request.setAttribute("listaAsignaciones",
                    asignacionDAO.listarPorAreaYTurno(
                            sesion.getIdArea(), sesion.getIdTurnoDefault()));
        }
        request.getRequestDispatcher("/jsp/consultar_asignacion.jsp")
               .forward(request, response);
    }

    private void cargarFormularioYForward(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {
        request.setAttribute("listaTurnos", turnoDAO.listarActivos());
        request.setAttribute("listaEmpleados",
                empleadoDAO.listarPorAreaYTurno(
                        sesion.getIdArea(), sesion.getIdTurnoDefault()));
        request.getRequestDispatcher("/jsp/asignar_turno.jsp")
               .forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — AdminArea y AdminRRHH
    // ─────────────────────────────────────────────────────────
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
