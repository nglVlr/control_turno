package controlTurnos.servlet;

import controlTurnos.dao.AreaDAO;
import controlTurnos.dao.BitacoraDAO;
import controlTurnos.dao.BitacoraDetalleDAO;
import controlTurnos.dao.EmpleadoDAO;
import controlTurnos.dao.RolDAO;
import controlTurnos.dao.TurnoDAO;
import controlTurnos.modelo.Empleado;
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
                request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                request.getRequestDispatcher("/jsp/consultar_empleado.jsp")
                       .forward(request, response);
                break;

            case "formularioAgregar":
                request.setAttribute("listaAreas",  areaDAO.listarActivas());
                request.setAttribute("listaTurnos", turnoDAO.listarActivos());
                request.getRequestDispatcher("/jsp/agregar_empleado.jsp")
                       .forward(request, response);
                break;

            // Carga AdminAreas por área+turno (llamado via AJAX desde agregar_empleado.jsp)
            case "adminAreasPorAreaYTurno":
                int idArea  = Integer.parseInt(request.getParameter("idArea"));
                int idTurno = Integer.parseInt(request.getParameter("idTurno"));
                request.setAttribute("listaAdmins",
                        empleadoDAO.listarAdminAreaPorAreaYTurno(idArea, idTurno));
                request.getRequestDispatcher("/jsp/combo_admins.jsp")
                       .forward(request, response);
                break;

            case "areas":
                request.setAttribute("listaAreas", areaDAO.listarActivas());
                request.getRequestDispatcher("/jsp/gestionar_areas.jsp")
                       .forward(request, response);
                break;

            case "roles":
                request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
                request.setAttribute("listaRoles",     rolDAO.listarActivos());
                request.getRequestDispatcher("/jsp/gestionar_roles.jsp")
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
            case "editarArea": editarArea(request, response, sesion);        break;
            case "cambiarRol": cambiarRol(request, response, sesion);        break;
            default:
                response.sendRedirect(request.getContextPath() + "/EmpleadoServlet?accion=listar");
        }
    }

    // ─────────────────────────────────────────────────────────
    // AGREGAR EMPLEADO — CU1 paso 7
    // Guarda id_admin_area y creado_por
    // Registra en bitácora + bitácora detalle
    // ─────────────────────────────────────────────────────────
    private void agregarEmpleado(HttpServletRequest request,
            HttpServletResponse response, Empleado sesion)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario").trim();

        if (empleadoDAO.existeUsuario(usuario)) {
            cargarCombosAgregar(request);
            request.setAttribute("error", "Error: El usuario ya existe en el sistema.");
            request.getRequestDispatcher("/jsp/agregar_empleado.jsp").forward(request, response);
            return;
        }

        Empleado emp = new Empleado();
        emp.setDpi(request.getParameter("dpi").trim());
        emp.setNombreCompleto(request.getParameter("nombreCompleto").trim());
        emp.setUsuario(usuario);
        emp.setContrasena(request.getParameter("contrasena").trim());
        emp.setCorreo(request.getParameter("correo").trim());
        emp.setIdArea(Integer.parseInt(request.getParameter("idArea")));
        emp.setIdTurnoDefault(Integer.parseInt(request.getParameter("idTurno")));
        emp.setIdAdminArea(Integer.parseInt(request.getParameter("idAdminArea")));
        emp.setCreadoPor(sesion.getIdEmpleado());

        boolean exito = empleadoDAO.agregar(emp);

        if (exito) {
            // Bitácora principal
            long idLog = bitacoraDAO.registrar(sesion.getIdEmpleado(), sesion.getUsuario(),
                    "Empleados", "Crear",
                    "Empleado creado: " + emp.getNombreCompleto());
            // Bitácora detalle — campos creados (valor_anterior = null = creación nueva)
            if (idLog > 0) {
                detalleDAO.registrar(idLog, null, "usuario",        null, emp.getUsuario());
                detalleDAO.registrar(idLog, null, "nombre_completo",null, emp.getNombreCompleto());
                detalleDAO.registrar(idLog, null, "id_area",        null, String.valueOf(emp.getIdArea()));
                detalleDAO.registrar(idLog, null, "id_turno",       null, String.valueOf(emp.getIdTurnoDefault()));
                detalleDAO.registrar(idLog, null, "id_admin_area",  null, String.valueOf(emp.getIdAdminArea()));
            }
            request.setAttribute("exito", "Empleado creado correctamente.");
        } else {
            request.setAttribute("error", "Error al registrar el empleado.");
        }

        cargarCombosAgregar(request);
        request.getRequestDispatcher("/jsp/agregar_empleado.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // INACTIVAR EMPLEADO — CU1-FA03
    // ─────────────────────────────────────────────────────────
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
                detalleDAO.registrar(idLog, idEmpleado, "estado",               "Activo",   "Inactivo");
                detalleDAO.registrar(idLog, idEmpleado, "motivo_inactivacion",  null,        motivo);
            }
        }

        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        request.setAttribute(exito ? "exito" : "error",
                exito ? "Empleado inactivado correctamente." : "Error al inactivar el empleado.");
        request.getRequestDispatcher("/jsp/consultar_empleado.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // EDITAR ÁREA
    // ─────────────────────────────────────────────────────────
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
        request.setAttribute("listaAreas", areaDAO.listarActivas());
        request.setAttribute(exito ? "exito" : "error",
                exito ? "Área actualizada correctamente." : "Error al actualizar el área.");
        request.getRequestDispatcher("/jsp/gestionar_areas.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // CAMBIAR ROL — FA09
    // Guarda modificado_por y registra detalle
    // ─────────────────────────────────────────────────────────
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
            request.getRequestDispatcher("/jsp/gestionar_roles.jsp").forward(request, response);
            return;
        }

        // Si nuevo rol es AdminArea, degradar al anterior si existe
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
                detalleDAO.registrar(idLog, idEmpleado, "id_rol", rolAnterior, rolNuevoNombre);
            }
            request.setAttribute("exito", "La asignación de rol ha sido exitosa.");
        } else {
            request.setAttribute("error", "Error al cambiar el rol.");
        }

        request.setAttribute("listaEmpleados", empleadoDAO.listarTodos());
        request.setAttribute("listaRoles",     rolDAO.listarActivos());
        request.getRequestDispatcher("/jsp/gestionar_roles.jsp").forward(request, response);
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private void cargarCombosAgregar(HttpServletRequest request) {
        request.setAttribute("listaAreas",  areaDAO.listarActivas());
        request.setAttribute("listaTurnos", turnoDAO.listarActivos());
    }

    // ─────────────────────────────────────────────────────────
    // VALIDAR SESIÓN — solo AdminRRHH
    // ─────────────────────────────────────────────────────────
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
