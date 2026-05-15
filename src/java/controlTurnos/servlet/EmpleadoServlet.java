package controlTurnos.servlet;

import controlTurnos.dao.AreaDAO;
import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.BitacoraDetalleDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.RolDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
import controlTurnos.util.CorreoService;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoDAO       empleadoDAO    = new EmpleadoDAO();
    private final AreaDAO            areaDAO        = new AreaDAO();
    private final TurnoDAO           turnoDAO       = new TurnoDAO();
    private final RolDAO             rolDAO         = new RolDAO();
    private final BitacoraDAO        bitacoraDAO    = new BitacoraDAO();
    private final BitacoraDetalleDAO detalleDAO     = new BitacoraDetalleDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesionRRHH(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) accion = "listar";

        switch (accion) {

            case "listar":
                cargarDashboard(request, "usuarios");
                request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp")
                       .forward(request, response);
                break;

            case "formularioAgregar":
                cargarDashboard(request, "agregar");
                request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp")
                       .forward(request, response);
                break;

            case "adminAreasPorAreaYTurno":
                try {
                    int idArea  = Integer.parseInt(request.getParameter("idArea"));
                    int idTurno = Integer.parseInt(request.getParameter("idTurno"));
                    request.setAttribute("listaAdmins",
                            empleadoDAO.listarAdminAreaPorAreaYTurno(idArea, idTurno));
                } catch (NumberFormatException ex) {
                    request.setAttribute("listaAdmins", new java.util.ArrayList<>());
                }
                request.getRequestDispatcher("/jsp/combo_admins.jsp")
                       .forward(request, response);
                break;

            case "areas":
                cargarDashboard(request, "areas");
                request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp")
                       .forward(request, response);
                break;

            case "roles":
                cargarDashboard(request, "roles");
                request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp")
                       .forward(request, response);
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Empleado sesion = validarSesionRRHH(request, response);
        if (sesion == null) return;

        String accion = request.getParameter("accion");
        if (accion == null) {
            response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
            return;
        }

        switch (accion) {
            case "agregar":    agregarEmpleado(request, response, sesion);   break;
            case "inactivar":  inactivarEmpleado(request, response, sesion); break;
            case "reactivar":  reactivarEmpleado(request, response, sesion); break;
            case "editarArea": editarArea(request, response, sesion);        break;
            case "cambiarRol": cambiarRol(request, response, sesion);        break;
            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
        }
    }

    private void agregarEmpleado(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario").trim();

        if (empleadoDAO.existeUsuario(usuario)) {
            cargarCombosAgregar(request);
            request.setAttribute("error", "Error: El usuario ya existe en el sistema.");
            request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
            return;
        }

        int idRol = 0;
        try { idRol = Integer.parseInt(request.getParameter("idRol")); }
        catch (NumberFormatException e) { idRol = 3; } // default Empleado

        // idAdminArea solo aplica para Empleado (rol=3)
        int idAdminArea = 0;
        try {
            String p = request.getParameter("idAdminAreaHidden");
            if (p != null && !p.trim().isEmpty()) idAdminArea = Integer.parseInt(p.trim());
        } catch (NumberFormatException e) { idAdminArea = 0; }

        int idArea  = 0;
        int idTurno = 0;
        try {
            String p = request.getParameter("idArea");
            if (p != null && !p.trim().isEmpty()) idArea = Integer.parseInt(p.trim());
        } catch (NumberFormatException e) { idArea = 0; }
        try {
            String p = request.getParameter("idTurno");
            if (p != null && !p.trim().isEmpty()) idTurno = Integer.parseInt(p.trim());
        } catch (NumberFormatException e) { idTurno = 0; }

        // Si es AdminRRHH y no eligió área, asignar a Recursos Humanos automáticamente
        if (idRol == 1 && idArea == 0) {
            idArea = 1; // Recursos Humanos
        }

        if (idRol == 3 && idAdminArea == 0) {
            cargarDashboard(request, "agregar");
            request.setAttribute("error", "Debes seleccionar un AdminArea para el empleado.");
            request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
            return;
        }

        Empleado emp = new Empleado();
        emp.setDpi(request.getParameter("dpi").trim());
        emp.setNombreCompleto(request.getParameter("nombreCompleto").trim());
        emp.setUsuario(usuario);
        emp.setContrasena(request.getParameter("contrasena").trim());
        emp.setCorreo(request.getParameter("correo").trim());
        emp.setIdArea(idArea);
        emp.setIdRol(idRol);
        emp.setIdTurnoDefault(idTurno);
        emp.setIdAdminArea(idAdminArea);
        emp.setCreadoPor(sesion.getIdEmpleado());

        boolean exito = empleadoDAO.agregarConRol(emp);

        if (exito) {
            // Capturar todos los valores ANTES del hilo — el request no es thread-safe
            final String correoDestino  = emp.getCorreo();
            final String nombreCompleto = emp.getNombreCompleto();
            final String usuarioTxt     = emp.getUsuario();
            final String contrasenaTxt  = request.getParameter("contrasena") != null
                                        ? request.getParameter("contrasena").trim() : "";

            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Empleados", "Crear",
                    "Usuario creado: " + emp.getNombreCompleto() + " | Rol id: " + idRol);

            if (idLog > 0) {
                Empleado creado = empleadoDAO.buscarPorUsuario(emp.getUsuario());

                detalleDAO.registrar(idLog, null, "usuario",         null, emp.getUsuario());
                detalleDAO.registrar(idLog, null, "nombre_completo", null, emp.getNombreCompleto());
                detalleDAO.registrar(idLog, null, "rol",             null,
                        creado != null ? creado.getNombreRol()   : String.valueOf(idRol));
                detalleDAO.registrar(idLog, null, "area",            null,
                        creado != null ? creado.getNombreArea()  : String.valueOf(idArea));
                detalleDAO.registrar(idLog, null, "turno",           null,
                        creado != null ? creado.getNombreTurno() : String.valueOf(idTurno));
                if (idAdminArea > 0) {
                    detalleDAO.registrar(idLog, null, "admin_area",  null,
                            creado != null ? creado.getNombreAdminArea() : String.valueOf(idAdminArea));
                }

                final String rolTxt   = creado != null && creado.getNombreRol()   != null ? creado.getNombreRol()   : "";
                final String areaTxt  = creado != null && creado.getNombreArea()  != null ? creado.getNombreArea()  : "";
                final String turnoTxt = creado != null && creado.getNombreTurno() != null ? creado.getNombreTurno() : "";

                new Thread(() -> {
                    CorreoService.enviarBienvenida(
                            correoDestino, nombreCompleto, usuarioTxt,
                            contrasenaTxt, rolTxt, areaTxt, turnoTxt);
                }).start();
            }
            request.setAttribute("exito", "Usuario creado correctamente. Se envió un correo de bienvenida.");
        } else {
            request.setAttribute("error", "Error al registrar el usuario.");
        }

        cargarDashboard(request, "agregar");
        request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
    }

    private void reactivarEmpleado(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int idEmpleado = Integer.parseInt(request.getParameter("idEmpleado"));
        Empleado antes = empleadoDAO.buscarPorId(idEmpleado);
        boolean exito  = empleadoDAO.reactivar(idEmpleado, sesion.getIdEmpleado());

        if (exito && antes != null) {
            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Empleados", "Crear",
                    "Empleado reactivado: " + antes.getNombreCompleto());
            if (idLog > 0) {
                detalleDAO.registrar(idLog, idEmpleado, "estado", "Inactivo", "Activo");
            }
        }

        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        request.setAttribute("tab", "usuarios");
        request.setAttribute(exito ? "exito" : "error",
                exito ? "Empleado reactivado correctamente." : "Error al reactivar el empleado.");
        request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
    }

    private void inactivarEmpleado(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int    idEmpleado = Integer.parseInt(request.getParameter("idEmpleado"));
        String motivo     = request.getParameter("motivoInactivacion");

        Empleado antes = empleadoDAO.buscarPorId(idEmpleado);
        boolean exito  = empleadoDAO.inactivar(idEmpleado, motivo, sesion.getIdEmpleado());

        if (exito && antes != null) {
            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Empleados", "Inactivar",
                    "Empleado inactivado: " + antes.getNombreCompleto() + " | Motivo: " + motivo);
            if (idLog > 0) {
                detalleDAO.registrar(idLog, idEmpleado, "estado",              "Activo",  "Inactivo");
                detalleDAO.registrar(idLog, idEmpleado, "motivo_inactivacion", "Ninguno", motivo);
            }
        }

        request.setAttribute(exito ? "exito" : "error",
                exito ? "Empleado inactivado correctamente." : "Error al inactivar el empleado.");
        cargarDashboard(request, "usuarios");
        request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
    }

    private void editarArea(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int    idArea      = Integer.parseInt(request.getParameter("idArea"));
        String descripcion = request.getParameter("descripcion").trim();

        boolean exito = areaDAO.editar(idArea, descripcion);
        if (exito) {
            bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Areas", "Crear", "Area id:" + idArea + " actualizada.");
        }
        request.setAttribute(exito ? "exito" : "error",
                exito ? "Área actualizada correctamente." : "Error al actualizar el área.");
        cargarDashboard(request, "areas");
        request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
    }

    private void cambiarRol(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        int idEmpleado = Integer.parseInt(request.getParameter("idEmpleado"));
        int idRolNuevo = Integer.parseInt(request.getParameter("idRol"));

        Empleado emp = empleadoDAO.buscarPorId(idEmpleado);
        if (emp == null) {
            request.setAttribute("error", "Empleado no encontrado.");
            request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
            request.setAttribute("listaRoles",     rolDAO.listarActivos());
            request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
            return;
        }

        // Si nuevo rol es AdminArea, degradar al anterior si existe (unicidad por área+turno)
        if (idRolNuevo == 2) {
            Empleado adminActual = empleadoDAO.buscarAdminAreaPorAreaYTurno(
                    emp.getIdArea(), emp.getIdTurnoDefault());
            if (adminActual != null && adminActual.getIdEmpleado() != idEmpleado) {
                empleadoDAO.cambiarRol(adminActual.getIdEmpleado(), 3, sesion.getIdEmpleado());
                long idLogDeg = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                        "Roles", "Crear",
                        "Rol degradado a Empleado: " + adminActual.getNombreCompleto());
                if (idLogDeg > 0) {
                    detalleDAO.registrar(idLogDeg, adminActual.getIdEmpleado(),
                            "id_rol", "AdminArea", "Empleado");
                }
            }
        }

        String rolAnterior = emp.getNombreRol();
        boolean exito = empleadoDAO.cambiarRol(idEmpleado, idRolNuevo, sesion.getIdEmpleado());

        if (exito) {
            String rolNuevoNombre = idRolNuevo == 1 ? "AdminRRHH"
                                  : idRolNuevo == 2 ? "AdminArea" : "Empleado";
            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Roles", "Crear",
                    "Rol cambiado: " + emp.getNombreCompleto()
                    + " | " + rolAnterior + " → " + rolNuevoNombre);
            if (idLog > 0) {
                // Guarda nombres de rol directamente — no IDs
                detalleDAO.registrar(idLog, idEmpleado, "rol", rolAnterior, rolNuevoNombre);
            }
            request.setAttribute("exito", "La asignación de rol ha sido exitosa.");
        } else {
            request.setAttribute("error", "Error al cambiar el rol.");
        }

        request.setAttribute(exito ? "exito" : "error",
                exito ? "La asignación de rol ha sido exitosa." : "Error al cambiar el rol.");
        cargarDashboard(request, "roles");
        request.getRequestDispatcher("/jsp/mantenimiento_usuarios.jsp").forward(request, response);
    }

    private void cargarDashboard(HttpServletRequest request, String tab) {
        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        request.setAttribute("listaAreas",     areaDAO.listarActivas());
        request.setAttribute("listaTurnos",    turnoDAO.listarActivos());
        request.setAttribute("listaRoles",     rolDAO.listarActivos());
        request.setAttribute("tab",            tab);
    }

    private void cargarCombosAgregar(HttpServletRequest request) {
        request.setAttribute("listaAreas",  areaDAO.listarActivas());
        request.setAttribute("listaTurnos", turnoDAO.listarActivos());
        request.setAttribute("listaRoles",  rolDAO.listarActivos());
    }

    private Empleado validarSesionRRHH(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("empleado") == null) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        Empleado sesion = (Empleado) session.getAttribute("empleado");
        if (!sesion.getNombreRol().equals("AdminRRHH")) {
            response.sendRedirect(request.getContextPath() + "/LoginServlet");
            return null;
        }
        return sesion;
    }
}
