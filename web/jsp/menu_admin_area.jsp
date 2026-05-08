<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="java.util.List"%>
<%
    Empleado emp = (Empleado) session.getAttribute("empleado");
    if (emp == null || !emp.getNombreRol().equals("AdminArea")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> nuevos = (List<Empleado>) session.getAttribute("notificacionNuevos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel AdminArea</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            Bienvenido, <span class="text-white fw-semibold"><%= emp.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= emp.getNombreRol() %>
            &nbsp;|&nbsp; <%= emp.getNombreArea() %>
            &nbsp;|&nbsp; <%= emp.getNombreTurno() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">

    <%-- Notificación informativa de nuevos empleados en las últimas 24 horas --%>
    <% if (nuevos != null && !nuevos.isEmpty()) { %>
        <div class="alert alert-info alert-dismissible fade show" role="alert" id="notifNuevos">
            <strong>📋 Nuevos empleados en tu área (últimas 24 horas):</strong>
            <ul class="mb-0 mt-1">
                <% for (Empleado n : nuevos) { %>
                    <li><strong><%= n.getNombreCompleto() %></strong> — Turno: <%= n.getNombreTurno() %></li>
                <% } %>
            </ul>
            <button type="button" class="btn-close" data-bs-dismiss="alert"
                    onclick="session.removeAttribute('notificacionNuevos')"></button>
        </div>
        <script>
            // Auto-cerrar la notificación después de 30 segundos
            setTimeout(function() {
                var el = document.getElementById('notifNuevos');
                if (el) { var bsAlert = new bootstrap.Alert(el); bsAlert.close(); }
            }, 30000);
        </script>
    <% } %>

    <h5 class="page-title">Panel de Administrador de Área</h5>
    <div class="row g-4">

        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/MarcajeServlet" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Marcaje</h6>
                    <p class="text-muted small mb-0">Registrar entrada, descansos y salida</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=listar" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Asignación de Turnos</h6>
                    <p class="text-muted small mb-0">Asignar turnos a tus empleados</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/SolicitudServlet?accion=menu" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Solicitudes</h6>
                    <p class="text-muted small mb-0">Gestionar licencias y notificaciones de cambios</p>
                </div>
            </a>
        </div>

    </div>
</div>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
