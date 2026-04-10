<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%
    Empleado empleado = (Empleado) session.getAttribute("empleado");
    if (empleado == null || !empleado.getNombreRol().equals("Empleado")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Empleado</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            Bienvenido, <span class="text-white fw-semibold"><%= empleado.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= empleado.getNombreArea() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <h5 class="page-title">Panel de Empleado</h5>
    <div class="row g-4">

        <div class="col-md-4">
            <a href="${pageContext.request.contextPath}/MarcajeServlet" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Marcaje</h6>
                    <p class="text-muted small mb-0">Registrar tu entrada, descansos y salida del día</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="#" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Gestiones del Empleado</h6>
                    <p class="text-muted small mb-0">Solicitar vacaciones, permisos, IGSS y más</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="#" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Cambio de Turno</h6>
                    <p class="text-muted small mb-0">Solicitar un cambio en tu turno asignado</p>
                </div>
            </a>
        </div>

    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
