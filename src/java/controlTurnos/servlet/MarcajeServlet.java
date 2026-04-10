package controlTurnos.servlet;

import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.MarcajeDAO;
import controlTurnos.modelo.Empleado;
import controlTurnos.modelo.Marcaje;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MarcajeServlet extends HttpServlet {

    private final MarcajeDAO   marcajeDAO  = new MarcajeDAO();
    private final BitacoraDAO  bitacoraDAO = new BitacoraDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        Marcaje marcajeHoy = marcajeDAO.obtenerMarcajeHoy(sesion.getIdEmpleado());
        request.setAttribute("marcajeHoy", marcajeHoy);
        request.getRequestDispatcher("/jsp/marcaje.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesion(request, response);
        if (sesion == null) return;

        // CORRECCIÓN Problema 6 — validar null antes del switch
        String accion = request.getParameter("accion");
        if (accion == null || accion.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/MarcajeServlet");
            return;
        }

        int idEmpleado = sesion.getIdEmpleado();
        String login   = sesion.getUsuario();
        String mensajeError = null;

        // Obtener marcaje actual para validar orden
        Marcaje marcajeHoy = marcajeDAO.obtenerMarcajeHoy(idEmpleado);

        switch (accion) {

            case "entrada":
                // CU2 paso 5 — no puede marcar si ya hay entrada hoy
                if (marcajeHoy != null && marcajeHoy.getHoraEntrada() != null) {
                    mensajeError = "Ya registraste tu entrada el día de hoy.";
                } else {
                    boolean exito = marcajeDAO.marcarEntrada(idEmpleado);
                    if (exito) {
                        // Recargar para verificar si fue tarde — CU2-FA02
                        Marcaje actualizado = marcajeDAO.obtenerMarcajeHoy(idEmpleado);
                        if (actualizado != null && actualizado.getEntradaTarde() == 1) {
                            request.setAttribute("exito", "Marcaje realizado con éxito.");
                            request.setAttribute("advertencia", "Entrada registrada tarde.");
                        } else {
                            request.setAttribute("exito", "Marcaje realizado con éxito.");
                        }
                        // CU2 paso 8 — guardar en bitácora AN01
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Entrada marcada por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar la entrada. Intente de nuevo.";
                    }
                }
                break;

            case "descanso1":
                // CU2-FA05 — mensaje exacto del CU
                if (marcajeHoy == null || marcajeHoy.getHoraEntrada() == null) {
                    mensajeError = "Debe marcar la entrada antes de registrar el descanso.";
                } else if (marcajeHoy.getHoraDescanso1() != null) {
                    mensajeError = "Ya registraste el primer descanso.";
                } else {
                    boolean exito = marcajeDAO.marcarDescanso1(idEmpleado);
                    if (exito) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        // CU2 paso 8 — bitácora
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Descanso 1 marcado por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar el descanso. Intente de nuevo.";
                    }
                }
                break;

            case "descanso2":
                // CU2-FA06 — mensaje exacto del CU
                if (marcajeHoy == null || marcajeHoy.getHoraDescanso1() == null) {
                    mensajeError = "Debe marcar el primer descanso antes de registrar el segundo descanso.";
                } else if (marcajeHoy.getHoraDescanso2() != null) {
                    mensajeError = "Ya registraste el segundo descanso.";
                } else {
                    boolean exito = marcajeDAO.marcarDescanso2(idEmpleado);
                    if (exito) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        // CU2 paso 8 — bitácora
                        bitacoraDAO.registrar(idEmpleado, login, "Marcaje", "Marcaje",
                                "Descanso 2 marcado por " + sesion.getNombreCompleto());
                    } else {
                        mensajeError = "Error al registrar el descanso. Intente de nuevo.";
                    }
                }
                break;

            case "salida":
                // CU2-FA08 — validar descanso 1 primero
                if (marcajeHoy == null || marcajeHoy.getHoraEntrada() == null) {
                    mensajeError = "Debe marcar la entrada antes de registrar la salida.";
                // CU2-FA08 — mensaje exacto del CU
                } else if (marcajeHoy.getHoraDescanso1() == null) {
                    mensajeError = "Debe marcar el primer descanso antes de registrar la salida.";
                // CU2-FA09 — mensaje exacto del CU
                } else if (marcajeHoy.getHoraDescanso2() == null) {
                    mensajeError = "Debe marcar el segundo descanso antes de registrar la salida.";
                } else if (marcajeHoy.getHoraSalida() != null) {
                    mensajeError = "Ya registraste tu salida el día de hoy.";
                } else {
                    boolean exito = marcajeDAO.marcarSalida(idEmpleado);
                    if (exito) {
                        request.setAttribute("exito", "Marcaje realizado con éxito.");
                        // CU2 paso 8 — bitácora
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

        // Recargar marcaje actualizado
        request.setAttribute("marcajeHoy", marcajeDAO.obtenerMarcajeHoy(idEmpleado));
        request.getRequestDispatcher("/jsp/marcaje.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — AdminArea y Empleado pueden marcar
    // ─────────────────────────────────────────────────────────
    private Empleado validarSesion(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
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
