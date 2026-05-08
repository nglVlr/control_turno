package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.SolicitudCambioTurnoDAO;
import controlTurnos.dao.SolicitudGestionDAO;
import controlTurnos.modelo.Empleado;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SolicitudServlet extends HttpServlet {

    private final SolicitudCambioTurnoDAO ctDAO      = new SolicitudCambioTurnoDAO();
    private final SolicitudGestionDAO     gestionDAO = new SolicitudGestionDAO();
    private final BitacoraDAO             bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "menu";

        String rol = sesion.getNombreRol();

        switch (accion) {

            // ── Menú de solicitudes según rol ──
            case "menu":
                if ("AdminRRHH".equals(rol)) {
                    // CU1-FA06: RRHH ve solicitudes de gestión aprobadas por AdminArea
                    request.setAttribute("listaSolicitudesGestion", gestionDAO.listarPendientesRRHH());
                    request.setAttribute("listaSolicitudesCambio",  ctDAO.listarPendientesRRHH());
                    request.getRequestDispatcher("/jsp/solicitudes_rrhh.jsp").forward(request, response);
                } else if ("AdminArea".equals(rol)) {
                    // CU4: AdminArea ve pendientes de sus empleados
                    cargarVistaSolicitudesAdminArea(request, sesion);
                    request.getRequestDispatcher("/jsp/solicitudes_admin_area.jsp").forward(request, response);
                }
                break;

            // ── Polling tiempo real — CU4 requerimiento entre pestañas ──
            case "poll":
                responderPoll(request, response, sesion);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?accion=menu");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/SolicitudServlet?accion=menu");
            return;
        }

        switch (accion) {
            case "resolverCambioTurno":
                resolverCambioTurno(request, response, sesion);
                break;
            case "resolverGestion":
                resolverGestion(request, response, sesion);
                break;
            case "marcarVistaOrigen":
                ctDAO.marcarVistaOrigen(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?accion=menu");
                break;
            case "marcarVistaDestino":
                ctDAO.marcarVistaDestino(Integer.parseInt(request.getParameter("id")));
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?accion=menu");
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/SolicitudServlet?accion=menu");
        }
    }

    // ─────────────────────────────────────────────────────────
    // RESOLVER CAMBIO DE TURNO — CU4 paso 8/FA02
    // Solo AdminRRHH puede Aprobar o Rechazar
    // Si aprueba: aplica el cambio en la tabla empleados
    // Notifica a AdminArea origen y destino (notif = Pendiente)
    // Tiempo real: la otra pestaña verá el cambio en el siguiente poll
    // ─────────────────────────────────────────────────────────
    private void resolverCambioTurno(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        if (!"AdminRRHH".equals(sesion.getNombreRol())) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return;
        }

        int    idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
        String decision    = request.getParameter("decision");   // Aprobado o Rechazado
        String observacion = request.getParameter("observacion");
        if (observacion == null) observacion = "";

        boolean exito = ctDAO.resolver(idSolicitud, sesion.getIdEmpleado(), decision, observacion);

        if (exito) {
            if ("Aprobado".equals(decision)) {
                ctDAO.aplicarCambio(idSolicitud);
            }
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Solicitudes", decision.equals("Aprobado") ? "Aprobar" : "Rechazar",
                    "Cambio de turno id:" + idSolicitud + " " + decision
                    + (observacion.isEmpty() ? "" : " | Obs: " + observacion));
            request.setAttribute("exito", "Solicitud " + decision.toLowerCase() + " correctamente.");
        } else {
            request.setAttribute("error", "La solicitud ya fue resuelta o no existe.");
        }

        request.setAttribute("listaSolicitudesGestion", gestionDAO.listarPendientesRRHH());
        request.setAttribute("listaSolicitudesCambio",  ctDAO.listarPendientesRRHH());
        request.getRequestDispatcher("/jsp/solicitudes_rrhh.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // RESOLVER GESTION — CU4-FA03/FA04 (AdminArea) y CU1-FA06 (RRHH)
    // AdminArea: Pendiente → Aprobada AdminArea / Rechazada AdminArea
    // RRHH: Aprobada AdminArea → Aprobada RRHH / Rechazada RRHH
    // ─────────────────────────────────────────────────────────
    private void resolverGestion(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int    idSolicitud = Integer.parseInt(request.getParameter("idSolicitud"));
        String decision    = request.getParameter("decision");
        String observacion = request.getParameter("observacion");
        if (observacion == null) observacion = "";

        boolean exito;
        String rol = sesion.getNombreRol();

        if ("AdminArea".equals(rol)) {
            exito = gestionDAO.resolverAdminArea(idSolicitud, sesion.getIdEmpleado(), decision, observacion);
            if (exito) {
                bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                        "Solicitudes", "Aprobar".equals(decision) ? "Aprobar" : "Rechazar",
                        "Gestion id:" + idSolicitud + " " + decision + " por AdminArea"
                        + (observacion.isEmpty() ? "" : " | Obs: " + observacion));
            }
            if (exito) request.setAttribute("exito", "Solicitud procesada correctamente.");
            else       request.setAttribute("error", "La solicitud ya fue procesada o no existe.");
            cargarVistaSolicitudesAdminArea(request, sesion);
            request.getRequestDispatcher("/jsp/solicitudes_admin_area.jsp").forward(request, response);

        } else if ("AdminRRHH".equals(rol)) {
            exito = gestionDAO.resolverRRHH(idSolicitud, sesion.getIdEmpleado(), decision, observacion);
            if (exito) {
                bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                        "Solicitudes", "Aprobar".equals(decision) ? "Aprobar" : "Rechazar",
                        "Gestion id:" + idSolicitud + " " + decision + " por RRHH"
                        + (observacion.isEmpty() ? "" : " | Obs: " + observacion));
            }
            if (exito) request.setAttribute("exito", "Solicitud procesada correctamente.");
            else       request.setAttribute("error", "La solicitud ya fue procesada o no existe.");
            request.setAttribute("listaSolicitudesGestion", gestionDAO.listarPendientesRRHH());
            request.setAttribute("listaSolicitudesCambio",  ctDAO.listarPendientesRRHH());
            request.getRequestDispatcher("/jsp/solicitudes_rrhh.jsp").forward(request, response);
        }
    }

    // ─────────────────────────────────────────────────────────
    // POLL — endpoint JSON para tiempo real entre pestañas
    // Devuelve conteo de pendientes para actualizar badges
    // ─────────────────────────────────────────────────────────
    private void responderPoll(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String rol = sesion.getNombreRol();
        int pendientes = 0;
        if ("AdminRRHH".equals(rol)) {
            pendientes = ctDAO.contarPendientesRRHH()
                       + gestionDAO.contarPendientesRRHH();
        } else if ("AdminArea".equals(rol)) {
            // Bug fix: contar por id_admin_area, no por área+turno
            pendientes = gestionDAO.contarPendientesPorAdminArea(sesion.getIdEmpleado())
                       + ctDAO.contarNotificacionesAdminArea(
                            sesion.getIdArea(), sesion.getIdTurnoDefault());
        }
        response.getWriter().write("{\"pendientes\":" + pendientes + "}");
    }

    // ─────────────────────────────────────────────────────────
    // CARGAR VISTA ADMINAREA
    // Bug fix: usa id del AdminArea, no área+turno
    // ─────────────────────────────────────────────────────────
    private void cargarVistaSolicitudesAdminArea(HttpServletRequest request, Empleado sesion) {
        // Solicitudes de gestión de SUS empleados (por id_admin_area)
        request.setAttribute("listaSolicitudesGestion",
                gestionDAO.listarPendientesPorAdminArea(sesion.getIdEmpleado()));
        // Notificaciones de cambio de turno que afectan su área
        request.setAttribute("listaSolicitudesCambio",
                ctDAO.listarNotificacionesAdminArea(
                        sesion.getIdArea(), sesion.getIdTurnoDefault()));
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — AdminRRHH y AdminArea
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
        if (!rol.equals("AdminRRHH") && !rol.equals("AdminArea")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
