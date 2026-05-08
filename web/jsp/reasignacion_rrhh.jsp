<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.util.AppConfig"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"AdminRRHH".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> lista = (List<Empleado>) request.getAttribute("listaEmpleados");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reasignación de Empleados</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos
        <span class="badge bg-secondary ms-1" style="font-size:0.65rem;vertical-align:middle;"><%=AppConfig.VERSION%></span>
    </span>
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
            <li class="breadcrumb-item active">Reasignación de Empleados</li>
        </ol>
    </nav>
    <h5 class="page-title">Reasignación de Empleados</h5>

    <div class="alert alert-info py-2 mb-3">
        <small>
            ℹ️ Desde aquí puedes cambiar el <strong>área, turno y AdminArea</strong> de cualquier empleado
            directamente, sin necesidad de que el empleado envíe una solicitud.
        </small>
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
               placeholder="Buscar por nombre, área o turno...">
    </div>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0" id="tablaEmpleados">
                <thead class="tabla-header">
                    <tr>
                        <th>#</th><th>Nombre</th><th>Área Actual</th>
                        <th>Turno Actual</th><th>Admin Área</th><th>Estado</th><th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (lista != null && !lista.isEmpty()) {
                        int i = 1;
                        for (Empleado e : lista) { %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><strong><%= e.getNombreCompleto() %></strong></td>
                            <td><%= e.getNombreArea() %></td>
                            <td><%= e.getNombreTurno() != null ? e.getNombreTurno() : "—" %></td>
                            <td><%= e.getNombreAdminArea() != null ? e.getNombreAdminArea() : "<span class='text-danger'>Sin asignar</span>" %></td>
                            <td>
                                <% if ("Activo".equals(e.getEstado())) { %>
                                    <span class="badge badge-activo px-2 py-1 rounded">Activo</span>
                                <% } else { %>
                                    <span class="badge badge-inactivo px-2 py-1 rounded">Inactivo</span>
                                <% } %>
                            </td>
                            <td>
                                <% if ("Activo".equals(e.getEstado()) && e.getIdRol() == 3) { %>
                                    <a href="${pageContext.request.contextPath}/ReasignacionServlet?accion=formularioReasignar&id=<%= e.getIdEmpleado() %>"
                                       class="btn btn-primary btn-sm">Reasignar</a>
                                <% } else { %>
                                    <span class="text-muted small">—</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } } else { %>
                        <tr><td colspan="7" class="text-center text-muted py-4">No hay empleados registrados.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('filtro').addEventListener('keyup', function() {
        const texto = this.value.toLowerCase();
        document.querySelectorAll('#tablaEmpleados tbody tr').forEach(f => {
            f.style.display = f.textContent.toLowerCase().includes(texto) ? '' : 'none';
        });
    });
</script>
</body>
</html>
