<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.AsignacionTurno"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null) { response.sendRedirect(request.getContextPath() + "/LoginServlet"); return; }
    String rol = sesion.getNombreRol();
    if (!"AdminArea".equals(rol) && !"AdminRRHH".equals(rol)) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<AsignacionTurno> lista = (List<AsignacionTurno>) request.getAttribute("listaAsignaciones");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Asignación de Turnos</title>
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
            <% if ("AdminArea".equals(rol)) { %>&nbsp;|&nbsp; <%= sesion.getNombreArea() %><% } %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/jsp/<%= "AdminRRHH".equals(rol) ? "menu_admin_rrhh" : "menu_admin_area" %>.jsp">Inicio</a>
            </li>
            <li class="breadcrumb-item active">Asignación de Turnos</li>
        </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="page-title mb-0">Asignación de Turnos</h5>
        <% if ("AdminArea".equals(rol)) { %>
            <a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=formularioAsignar" class="btn btn-primary btn-sm">+ Asignar Turno</a>
        <% } %>
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

    <% if ("AdminArea".equals(rol)) { %>
        <div class="alert alert-info py-2 mb-3">
            <small>📋 Mostrando asignaciones de <strong>tus empleados</strong> en <strong><%= sesion.getNombreArea() %></strong> — Turno <strong><%= sesion.getNombreTurno() %></strong></small>
        </div>
    <% } %>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="tabla-header">
                    <tr><th>#</th><th>Empleado</th><th>Turno</th><th>Fecha Inicio</th><th>Fecha Fin</th><th>Estado</th><th>Asignado por</th></tr>
                </thead>
                <tbody>
                    <% if (lista != null && !lista.isEmpty()) {
                        int i = 1;
                        for (AsignacionTurno at : lista) { %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><%= at.getNombreEmpleado() %></td>
                            <td><%= at.getNombreTurno() %></td>
                            <td><%= at.getFechaInicio() %></td>
                            <td><%= at.getFechaFin() %></td>
                            <td>
                                <% if ("Vigente".equals(at.getEstado())) { %>
                                    <span class="badge badge-vigente px-2 py-1 rounded">Vigente</span>
                                <% } else if ("Modificada".equals(at.getEstado())) { %>
                                    <span class="badge badge-pendiente px-2 py-1 rounded">Modificada</span>
                                <% } else { %>
                                    <span class="badge badge-inactivo px-2 py-1 rounded">Cancelada</span>
                                <% } %>
                            </td>
                            <td><%= at.getNombreAdmin() %></td>
                        </tr>
                    <% } } else { %>
                        <tr><td colspan="7" class="text-center text-muted py-4">No hay asignaciones registradas.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/<%= "AdminRRHH".equals(rol) ? "menu_admin_rrhh" : "menu_admin_area" %>.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
