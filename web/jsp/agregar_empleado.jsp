<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="controlTurnos.modelo.Rol"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet");
        return;
    }
    List<Area>  areas  = (List<Area>)  request.getAttribute("listaAreas");
    List<Turno> turnos = (List<Turno>) request.getAttribute("listaTurnos");
    List<Rol>   roles  = (List<Rol>)   request.getAttribute("listaRoles");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Empleado</title>
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
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/EmpleadoServlet?accion=listar">Usuarios</a></li>
            <li class="breadcrumb-item active">Agregar Empleado</li>
        </ol>
    </nav>

    <h5 class="page-title">Agregar Empleado</h5>

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

    <div class="card shadow-sm border-0" style="max-width:700px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="agregar">

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">No. DPI <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="dpi"
                               placeholder="0000 00000 0000" required maxlength="20">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Nombre Completo <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="nombreCompleto"
                               placeholder="Nombre completo" required maxlength="150">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Usuario <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="usuario"
                               placeholder="Nombre de usuario" required maxlength="50">
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Correo <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" name="correo"
                               placeholder="correo@empresa.com" required maxlength="120">
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idArea" required>
                            <option value="">Seleccionar...</option>
                            <% if (areas != null) { for (Area a : areas) { %>
                                <option value="<%= a.getIdArea() %>"><%= a.getNombreArea() %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Turno <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurno" required>
                            <option value="">Seleccionar...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>"><%= t.getNombreTurno() %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Rol <span class="text-danger">*</span></label>
                        <select class="form-select" name="idRol" required>
                            <option value="">Seleccionar...</option>
                            <% if (roles != null) { for (Rol r : roles) { %>
                                <option value="<%= r.getIdRol() %>"><%= r.getNombreRol() %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Contraseña <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" name="contrasena"
                               placeholder="Contraseña del empleado" required maxlength="255">
                    </div>
                </div>

                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Registrar</button>
                    <a href="${pageContext.request.contextPath}/EmpleadoServlet?accion=listar" class="btn btn-secondary">Regresar</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
