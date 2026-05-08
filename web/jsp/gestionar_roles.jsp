<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Rol"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> lista  = (List<Empleado>) request.getAttribute("listaEmpleados");
    List<Rol>      roles  = (List<Rol>)      request.getAttribute("listaRoles");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Roles</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreRol() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp">Inicio</a></li>
            <li class="breadcrumb-item active">Gestión de Roles</li>
        </ol>
    </nav>
    <h5 class="page-title">Gestión de Roles</h5>

    <div class="alert alert-info py-2 mb-3">
        <small>
            ℹ️ Solo puede existir <strong>un AdminArea por área y turno</strong>.
            Si asignas AdminArea a un empleado y ya existe uno en esa combinación,
            el actual será degradado a Empleado automáticamente.
        </small>
    </div>

    <% if (request.getAttribute("exito") != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= request.getAttribute("exito") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <!-- Filtro -->
    <div class="mb-3">
        <input type="text" id="filtro" class="form-control" style="max-width:350px;"
               placeholder="Buscar por nombre, área o turno...">
    </div>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0" id="tablaRoles">
                <thead class="tabla-header">
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Usuario</th>
                        <th>Área</th>
                        <th>Turno</th>
                        <th>Rol Actual</th>
                        <th>Estado</th>
                        <th>Cambiar Rol</th>
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
                            <td>
                                <%
                                    String badgeRol = "badge-vigente";
                                    if ("AdminRRHH".equals(e.getNombreRol())) badgeRol = "badge-aprobado";
                                    else if ("AdminArea".equals(e.getNombreRol())) badgeRol = "badge-pendiente";
                                    else badgeRol = "badge-vigente";
                                %>
                                <span class="badge <%= badgeRol %> px-2 py-1 rounded">
                                    <%= e.getNombreRol() %>
                                </span>
                            </td>
                            <td>
                                <% if ("Activo".equals(e.getEstado())) { %>
                                    <span class="badge badge-activo px-2 py-1 rounded">Activo</span>
                                <% } else { %>
                                    <span class="badge badge-inactivo px-2 py-1 rounded">Inactivo</span>
                                <% } %>
                            </td>
                            <td>
                                <% if ("Activo".equals(e.getEstado())) { %>
                                    <button class="btn btn-primary btn-sm"
                                        onclick="abrirCambiarRol(
                                            <%= e.getIdEmpleado() %>,
                                            '<%= e.getNombreCompleto() %>',
                                            '<%= e.getNombreArea() %>',
                                            '<%= e.getNombreTurno() != null ? e.getNombreTurno() : "Sin turno" %>',
                                            <%= e.getIdRol() %>)">
                                        Cambiar Rol
                                    </button>
                                <% } else { %>
                                    <span class="text-muted small">—</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } } else { %>
                        <tr><td colspan="8" class="text-center text-muted py-4">No hay empleados registrados.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<!-- Modal Cambiar Rol — FA09 -->
<div class="modal fade" id="modalCambiarRol" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Cambiar Rol</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="cambiarRol">
                <input type="hidden" name="idEmpleado" id="idEmpleadoModal">
                <div class="modal-body">
                    <p class="mb-1">Empleado: <strong id="nombreEmpleadoModal"></strong></p>
                    <p class="mb-3 text-muted small">
                        Área: <span id="areaModal"></span> &nbsp;|&nbsp;
                        Turno: <span id="turnoModal"></span>
                    </p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Nuevo Rol <span class="text-danger">*</span></label>
                        <select class="form-select" name="idRol" id="selectRol" required>
                            <option value="">Seleccionar rol...</option>
                            <% if (roles != null) { for (Rol r : roles) { %>
                                <option value="<%= r.getIdRol() %>"><%= r.getNombreRol() %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="alert alert-warning py-2 small" id="alertaAdminArea" style="display:none;">
                        ⚠️ Si ya existe un AdminArea en esta área y turno, será degradado a Empleado automáticamente.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Confirmar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    function abrirCambiarRol(id, nombre, area, turno, rolActual) {
        document.getElementById('idEmpleadoModal').value = id;
        document.getElementById('nombreEmpleadoModal').textContent = nombre;
        document.getElementById('areaModal').textContent = area;
        document.getElementById('turnoModal').textContent = turno;
        // Preseleccionar el rol actual
        const select = document.getElementById('selectRol');
        select.value = rolActual;
        new bootstrap.Modal(document.getElementById('modalCambiarRol')).show();
    }

    // Mostrar alerta si selecciona AdminArea
    document.getElementById('selectRol').addEventListener('change', function() {
        const alerta = document.getElementById('alertaAdminArea');
        alerta.style.display = (this.value === '2') ? 'block' : 'none';
    });

    // Filtro en tiempo real
    document.getElementById('filtro').addEventListener('keyup', function() {
        const texto = this.value.toLowerCase();
        document.querySelectorAll('#tablaRoles tbody tr').forEach(f => {
            f.style.display = f.textContent.toLowerCase().includes(texto) ? '' : 'none';
        });
    });
</script>
</body>
</html>
