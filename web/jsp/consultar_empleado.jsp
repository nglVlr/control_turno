<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> lista = (List<Empleado>) request.getAttribute("listaEmpleados");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consultar Usuarios</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; AdminRRHH
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp">Inicio</a></li>
            <li class="breadcrumb-item active">Usuarios</li>
        </ol>
    </nav>
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="page-title mb-0">Consultar Usuarios</h5>
        <a href="${pageContext.request.contextPath}/EmpleadoServlet?accion=formularioAgregar" class="btn btn-primary btn-sm">+ Agregar Empleado</a>
    </div>

    <% if (request.getAttribute("exito") != null) { %>
        <div class="alert alert-success alert-dismissible fade show">
            <%= request.getAttribute("exito") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <div class="mb-3">
        <input type="text" id="filtro" class="form-control" style="max-width:350px;"
               placeholder="Buscar por nombre, área, turno o admin...">
    </div>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0" id="tablaEmpleados">
                <thead class="tabla-header">
                    <tr>
                        <th>#</th><th>Nombre</th><th>Usuario</th><th>Área</th>
                        <th>Turno</th><th>Rol</th><th>Admin Área</th>
                        <th>Estado</th><th>Creado por</th><th>Modificado por</th><th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (lista != null && !lista.isEmpty()) {
                        int i = 1;
                        for (Empleado e : lista) { %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><%= e.getNombreCompleto() %></td>
                            <td><%= e.getUsuario() %></td>
                            <td><%= e.getNombreArea() %></td>
                            <td><%= e.getNombreTurno() != null ? e.getNombreTurno() : "—" %></td>
                            <td><%= e.getNombreRol() %></td>
                            <td><%= e.getNombreAdminArea() != null ? e.getNombreAdminArea() : "—" %></td>
                            <td>
                                <% if ("Activo".equals(e.getEstado())) { %>
                                    <span class="badge badge-activo px-2 py-1 rounded">Activo</span>
                                <% } else { %>
                                    <span class="badge badge-inactivo px-2 py-1 rounded">Inactivo</span>
                                <% } %>
                            </td>
                            <td><small><%= e.getCreadoPorNombre() != null ? e.getCreadoPorNombre() : "—" %></small></td>
                            <td><small><%= e.getModificadoPorNombre() != null ? e.getModificadoPorNombre() : "—" %></small></td>
                            <td>
                                <% if ("Activo".equals(e.getEstado())) { %>
                                    <button class="btn btn-danger btn-sm"
                                        onclick="confirmarInactivar(<%= e.getIdEmpleado() %>, '<%= e.getNombreCompleto() %>')">
                                        Inactivar
                                    </button>
                                <% } else { %>
                                    <span class="text-muted small">—</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } } else { %>
                        <tr><td colspan="11" class="text-center text-muted py-4">No hay empleados registrados.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<!-- Modal Inactivar -->
<div class="modal fade" id="modalInactivar" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Inactivar Empleado</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="inactivar">
                <input type="hidden" name="idEmpleado" id="idEmpleadoModal">
                <div class="modal-body">
                    <p>Empleado: <strong id="nombreEmpleadoModal"></strong></p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Motivo <span class="text-danger">*</span></label>
                        <select class="form-select" name="motivoInactivacion" required>
                            <option value="">Seleccionar...</option>
                            <option value="Permiso Personal">Permiso Personal</option>
                            <option value="Vacaciones">Vacaciones</option>
                            <option value="Cita al IGSS">Cita al IGSS</option>
                            <option value="Licencia de Cumpleanos">Licencia de Cumpleaños</option>
                            <option value="Suspension Laboral">Suspensión Laboral</option>
                            <option value="Otros">Otros</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-danger">Aceptar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmarInactivar(id, nombre) {
        document.getElementById('idEmpleadoModal').value = id;
        document.getElementById('nombreEmpleadoModal').textContent = nombre;
        new bootstrap.Modal(document.getElementById('modalInactivar')).show();
    }
    document.getElementById('filtro').addEventListener('keyup', function() {
        const texto = this.value.toLowerCase();
        document.querySelectorAll('#tablaEmpleados tbody tr').forEach(f => {
            f.style.display = f.textContent.toLowerCase().includes(texto) ? '' : 'none';
        });
    });
</script>
</body>
</html>
