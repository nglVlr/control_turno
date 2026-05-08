package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.MarcajeDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
import controlTurnos.modelo.Marcaje;
import controlTurnos.modelo.Turno;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MarcajeServlet extends HttpServlet {

    private final MarcajeDAO  marcajeDAO  = new MarcajeDAO();
    private final TurnoDAO    turnoDAO    = new TurnoDAO();
    private final BitacoraDAO bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        // AdminRRHH ve todos los marcajes del día — solo visual
        if ("AdminRRHH".equals(sesion.getNombreRol())) {
            request.setAttribute("listaMarcajes", marcajeDAO.listarTodosMarcajesHoy());
            request.getRequestDispatcher("/jsp/marcaje_rrhh.jsp").forward(request, response);
            return;
        }

        cargarVista(request, sesion);
        request.getRequestDispatcher("/jsp/marcaje.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        // RRHH no puede marcar — solo visual
        if ("AdminRRHH".equals(sesion.getNombreRol())) {
            response.sendRedirect(request.getContextPath() + "/MarcajeServlet");
            return;
        }

        String accion = request.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/MarcajeServlet");
            return;
        }

        int    idEmpleado = sesion.getIdEmpleado();
        String login      = sesion.getUsuario();
        String mensajeError = null;

        // Obtener turno del empleado para validar horario
        Turno turno = turnoDAO.buscarPorId(sesion.getIdTurnoDefault());

        Marcaje marcajeHoy = marcajeDAO.obtenerMarcajeHoy(idEmpleado);

        switch (accion) {

            case "entrada":
                if (marcajeHoy != null && marcajeHoy.getHoraEntrada() != null) {
                    mensajeError = "Ya registraste tu entrada el día de hoy.";
                // Validar que esté en su horario de turno
                } else if (!marcajeDAO.estaEnHorario(turno)) {
                    mensajeError = "No puedes marcar fuera de tu horario de turno ("
                            + (turno != null ? turno.getHoraInicio() + " - " + turno.getHoraFin() : "") + ").";
                } else {
                    boolean ok = marcajeDAO.marcarEntrada(idEmpleado, turno);
                    if (ok) {
                        Marcaje actualizado = marcajeDAO.obtenerMarcajeHoy(idEmpleado);
                        if (actualizado != null && actualizado.getEntradaTarde() == 1) {
                            request.setAttribute("advertencia", "Entrada registrada tarde.");
                        }
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Entrada marcada por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar la entrada. Intente de nuevo.";
                    }
                }
                break;

            case "descanso1":
                if (marcajeHoy == null || marcajeHoy.getHoraEntrada() == null) {
                    mensajeError = "Debe marcar la entrada antes de registrar el descanso.";
                } else if (marcajeHoy.getHoraDescanso1() != null) {
                    mensajeError = "Ya registraste el primer descanso.";
                } else {
                    boolean ok = marcajeDAO.marcarDescanso1(idEmpleado);
                    if (ok) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Descanso 1 marcado por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar el descanso. Intente de nuevo.";
                    }
                }
                break;

            case "descanso2":
                if (marcajeHoy == null || marcajeHoy.getHoraDescanso1() == null) {
                    mensajeError = "Debe marcar el primer descanso antes de registrar el segundo descanso.";
                } else if (marcajeHoy.getHoraDescanso2() != null) {
                    mensajeError = "Ya registraste el segundo descanso.";
                } else {
                    boolean ok = marcajeDAO.marcarDescanso2(idEmpleado);
                    if (ok) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Descanso 2 marcado por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar el descanso. Intente de nuevo.";
                    }
                }
                break;

            case "salida":
                if (marcajeHoy == null || marcajeHoy.getHoraEntrada() == null) {
                    mensajeError = "Debe marcar la entrada antes de registrar la salida.";
                } else if (marcajeHoy.getHoraDescanso1() == null) {
                    mensajeError = "Debe marcar el primer descanso antes de registrar la salida.";
                } else if (marcajeHoy.getHoraDescanso2() == null) {
                    mensajeError = "Debe marcar el segundo descanso antes de registrar la salida.";
                } else if (marcajeHoy.getHoraSalida() != null) {
                    mensajeError = "Ya registraste tu salida el día de hoy.";
                } else {
                    boolean ok = marcajeDAO.marcarSalida(idEmpleado);
                    if (ok) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Salida marcada por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar la salida. Intente de nuevo.";
                    }
                }
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/MarcajeServlet");
                return;
        }

        if (mensajeError != null) {
            request.setAttribute("error", mensajeError);
        }

        cargarVista(request, sesion);
        request.getRequestDispatcher("/jsp/marcaje.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // CARGAR VISTA
    // AdminArea ve marcajes de SUS empleados (id_admin_area)
    // ─────────────────────────────────────────────────────────
    private void cargarVista(HttpServletRequest request, Empleado sesion) {
        Turno turno = turnoDAO.buscarPorId(sesion.getIdTurnoDefault());
        request.setAttribute("turno", turno);
        request.setAttribute("marcajeHoy",
                marcajeDAO.obtenerMarcajeHoy(sesion.getIdEmpleado()));
        if ("AdminArea".equals(sesion.getNombreRol())) {
            request.setAttribute("listaMarcajes",
                    marcajeDAO.listarMarcajesHoyPorAdminArea(sesion.getIdEmpleado()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — AdminRRHH, AdminArea y Empleado
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
        if (!rol.equals("AdminArea") && !rol.equals("Empleado")
                && !rol.equals("AdminRRHH")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
