package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.MarcajeDAO;
import controlTurnos.modelo.Empleado;
import controlTurnos.modelo.Marcaje;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MarcajeServlet extends HttpServlet {

    private final MarcajeDAO  marcajeDAO  = new MarcajeDAO();
    private final BitacoraDAO bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        cargarVista(request, sesion);
        request.getRequestDispatcher("/jsp/marcaje.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/MarcajeServlet");
            return;
        }

        int    idEmpleado = sesion.getIdEmpleado();
        String login      = sesion.getUsuario();
        String mensajeError = null;

        Marcaje marcajeHoy = marcajeDAO.obtenerMarcajeHoy(idEmpleado);

        switch (accion) {

            case "entrada":
                if (marcajeHoy != null && marcajeHoy.getHoraEntrada() != null) {
                    mensajeError = "Ya registraste tu entrada el día de hoy.";
                } else {
                    boolean ok = marcajeDAO.marcarEntrada(idEmpleado);
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
                // CU2-FA05
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
                // CU2-FA06
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
                // CU2-FA08 y FA09
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
    // CARGAR VISTA — marcaje del día + lista de marcajes del área
    // AdminArea ve también los marcajes de sus empleados
    // ─────────────────────────────────────────────────────────
    private void cargarVista(HttpServletRequest request, Empleado sesion) {
        request.setAttribute("marcajeHoy",
                marcajeDAO.obtenerMarcajeHoy(sesion.getIdEmpleado()));

        // Si es AdminArea, también carga la lista de marcajes de su área y turno
        if ("AdminArea".equals(sesion.getNombreRol())) {
            request.setAttribute("listaMarcajes",
                    marcajeDAO.listarMarcajesHoyPorAreaYTurno(
                            sesion.getIdArea(), sesion.getIdTurnoDefault()));
        }
    }

    // ─────────────────────────────────────────────────────────
    // POLLING — endpoint para tiempo real
    // El JSP llama a /MarcajeServlet?accion=poll cada 15 segundos
    // Devuelve JSON con el estado actual del marcaje
    // ─────────────────────────────────────────────────────────
    private void poll(HttpServletRequest request, HttpServletResponse response,
            Empleado sesion) throws IOException {
        Marcaje m = marcajeDAO.obtenerMarcajeHoy(sesion.getIdEmpleado());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        StringBuilder json = new StringBuilder("{");
        json.append("\"entrada\":\"").append(m != null && m.getHoraEntrada()   != null ? m.getHoraEntrada()   : "").append("\",");
        json.append("\"descanso1\":\"").append(m != null && m.getHoraDescanso1() != null ? m.getHoraDescanso1() : "").append("\",");
        json.append("\"descanso2\":\"").append(m != null && m.getHoraDescanso2() != null ? m.getHoraDescanso2() : "").append("\",");
        json.append("\"salida\":\"").append(m != null && m.getHoraSalida()    != null ? m.getHoraSalida()    : "").append("\",");
        json.append("\"tarde\":").append(m != null ? m.getEntradaTarde() : 0);
        json.append("}");
        response.getWriter().write(json.toString());
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — AdminArea y Empleado pueden marcar
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
        if (!rol.equals("AdminArea") && !rol.equals("Empleado")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
