<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%
    Empleado empleado = (Empleado) session.getAttribute("empleado");
    if (empleado == null || !empleado.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel AdminRRHH</title>
    <link rel="stylesheet" href="../css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/style.css">
</head>
<body>

<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            Bienvenido, <span class="text-white fw-semibold"><%= empleado.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= empleado.getNombreRol() %>
        </span>
        <a href="../LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <h5 class="page-title">Panel de Administrador RRHH</h5>
    <div class="row g-4">

        <div class="col-md-4">
            <a href="../EmpleadoServlet?accion=listar" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Mantenimiento de Usuarios</h6>
                    <p class="text-muted small mb-0">Agregar, consultar y gestionar empleados</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="#" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Gestión de Roles</h6>
                    <p class="text-muted small mb-0">Asignar y eliminar roles a los empleados</p>
                </div>
            </a>
        </div>

        <div class="col-md-4">
            <a href="#" class="card menu-card shadow-sm h-100 d-block p-3">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Solicitudes</h6>
                    <p class="text-muted small mb-0">Aprobar o rechazar solicitudes de licencias</p>
                </div>
            </a>
        </div>

    </div>
</div>

<script src="../js/bootstrap.bundle.min.js"></script>
</body>
</html>
