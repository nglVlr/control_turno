package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.SolicitudCambioTurnoDAO;
import controlTurnos.dao.SolicitudGestionDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
import controlTurnos.modelo.SolicitudCambioTurno;
import controlTurnos.modelo.SolicitudGestion;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmpleadoGestionServlet extends HttpServlet {

    private final SolicitudGestionDAO     gestionDAO = new SolicitudGestionDAO();
    private final SolicitudCambioTurnoDAO ctDAO      = new SolicitudCambioTurnoDAO();
    private final TurnoDAO                turnoDAO   = new TurnoDAO();
    private final BitacoraDAO             bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "menu";

        switch (accion) {

            case "menu":
                // Historial de gestiones del empleado
                request.setAttribute("listaGestiones",
                        gestionDAO.listarPorEmpleado(sesion.getIdEmpleado()));
                request.setAttribute("listaCambios",
                        ctDAO.listarPorEmpleado(sesion.getIdEmpleado()));
                request.getRequestDispatcher("/jsp/gestiones_empleado.jsp")
                       .forward(request, response);
                break;

            case "formularioGestion":
                // CU5 paso 5 — formulario de gestiones
                request.getRequestDispatcher("/jsp/solicitar_gestion.jsp")
                       .forward(request, response);
                break;

            case "formularioCambioTurno":
                // CU5-FA02 — formulario cambio de turno
                request.setAttribute("listaTurnos", turnoDAO.listarActivos());
                request.getRequestDispatcher("/jsp/solicitar_cambio_turno.jsp")
                       .forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoGestionServlet?accion=menu");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoGestionServlet?accion=menu");
            return;
        }

        switch (accion) {
            case "crearGestion":
                crearGestion(request, response, sesion);
                break;
            case "crearCambioTurno":
                crearCambioTurno(request, response, sesion);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoGestionServlet?accion=menu");
        }
    }

    // ─────────────────────────────────────────────────────────
    // CREAR GESTIÓN — CU5 paso 8
    // Empleado solicita vacaciones, permisos, IGSS, etc.
    // Estado inicial: Pendiente — AdminArea de su área y turno la verá
    // ─────────────────────────────────────────────────────────
    private void crearGestion(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        SolicitudGestion s = new SolicitudGestion();
        s.setIdEmpleado(sesion.getIdEmpleado());
        s.setIdTipoGestion(Integer.parseInt(request.getParameter("idTipoGestion")));
        s.setFechaInicio(request.getParameter("fechaInicio"));
        s.setFechaFin(request.getParameter("fechaFin"));
        s.setMotivo(request.getParameter("motivo") != null
                ? request.getParameter("motivo").trim() : "");

        boolean exito = gestionDAO.crear(s);

        if (exito) {
            // CU5 paso 10 — bitácora
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Gestiones", "Solicitar",
                    "Gestión creada por " + sesion.getNombreCompleto()
                    + " | Tipo id:" + s.getIdTipoGestion()
                    + " | " + s.getFechaInicio() + " al " + s.getFechaFin());
            request.setAttribute("exito", "Gestión creada con éxito");
        } else {
            request.setAttribute("error", "Error al crear la gestión. Intente de nuevo.");
        }

        request.setAttribute("listaGestiones",
                gestionDAO.listarPorEmpleado(sesion.getIdEmpleado()));
        request.setAttribute("listaCambios",
                ctDAO.listarPorEmpleado(sesion.getIdEmpleado()));
        request.getRequestDispatcher("/jsp/gestiones_empleado.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // CREAR CAMBIO DE TURNO — CU5-FA02
    // Empleado solicita cambio de turno/área
    // Estado inicial: Pendiente — lo resuelve el RRHH
    // Si es mismo área, origen=destino pero turno diferente
    // ─────────────────────────────────────────────────────────
    private void crearCambioTurno(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        SolicitudCambioTurno s = new SolicitudCambioTurno();
        s.setIdEmpleado(sesion.getIdEmpleado());
        s.setFechaInicial(request.getParameter("fechaInicial"));
        s.setIdTurnoInicial(sesion.getIdTurnoDefault()); // turno actual del empleado
        s.setIdAreaOrigen(sesion.getIdArea());            // área actual del empleado
        s.setFechaNueva(request.getParameter("fechaNueva"));
        s.setIdTurnoNuevo(Integer.parseInt(request.getParameter("idTurnoNuevo")));
        s.setIdAreaDestino(Integer.parseInt(request.getParameter("idAreaDestino")));
        s.setJustificacion(request.getParameter("justificacion") != null
                ? request.getParameter("justificacion").trim() : "");

        // No puede solicitar el mismo turno y área que ya tiene
        if (s.getIdTurnoNuevo() == s.getIdTurnoInicial()
                && s.getIdAreaDestino() == s.getIdAreaOrigen()) {
            request.setAttribute("error",
                    "No puedes solicitar el mismo turno y área que ya tienes asignado.");
            request.setAttribute("listaTurnos", turnoDAO.listarActivos());
            request.getRequestDispatcher("/jsp/solicitar_cambio_turno.jsp").forward(request, response);
            return;
        }

        boolean exito = ctDAO.crear(s);

        if (exito) {
            // CU5-FA02 paso 11 — bitácora
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Cambio Turno", "Solicitar",
                    "Cambio de turno solicitado por " + sesion.getNombreCompleto()
                    + " | Turno nuevo id:" + s.getIdTurnoNuevo()
                    + " | Area destino id:" + s.getIdAreaDestino());
            request.setAttribute("exito", "Cambio de turno solicitado con éxito");
        } else {
            request.setAttribute("error", "Error al enviar la solicitud. Intente de nuevo.");
        }

        request.setAttribute("listaGestiones",
                gestionDAO.listarPorEmpleado(sesion.getIdEmpleado()));
        request.setAttribute("listaCambios",
                ctDAO.listarPorEmpleado(sesion.getIdEmpleado()));
        request.getRequestDispatcher("/jsp/gestiones_empleado.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — todos los roles pueden ver sus gestiones
    // ─────────────────────────────────────────────────────────
    private Empleado validarSesion(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("empleado") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        // Cualquier empleado logueado puede gestionar sus propias solicitudes
        return (Empleado) session.getAttribute("empleado");
    }
}
