<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null) { response.sendRedirect(request.getContextPath() + "/LoginServlet"); return; }
    String rol = sesion.getNombreRol();
    if (!"AdminArea".equals(rol) && !"AdminRRHH".equals(rol)) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> empleados = (List<Empleado>) request.getAttribute("listaEmpleados");
    List<Turno>    turnos    = (List<Turno>)    request.getAttribute("listaTurnos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Asignar Turno</title>
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
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=listar">Asignaciones</a></li>
            <li class="breadcrumb-item active">Asignar Turno</li>
        </ol>
    </nav>
    <h5 class="page-title">Asignar Turno</h5>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <%-- Nota: AdminArea solo ve empleados de su área y turno --%>
    <% if ("AdminArea".equals(rol)) { %>
        <div class="alert alert-info py-2 mb-3">
            <small>📋 Solo se muestran los empleados de <strong><%= sesion.getNombreArea() %></strong> — Turno <strong><%= sesion.getNombreTurno() %></strong></small>
        </div>
    <% } %>

    <div class="card shadow-sm border-0" style="max-width:600px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/AsignacionTurnoServlet" method="post">
                <input type="hidden" name="accion" value="asignar">
                <div class="row g-3">

                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Empleado <span class="text-danger">*</span></label>
                        <select class="form-select" name="idEmpleado" required>
                            <option value="">Seleccionar empleado...</option>
                            <% if (empleados != null) { for (Empleado e : empleados) { %>
                                <option value="<%= e.getIdEmpleado() %>">
                                    <%= e.getNombreCompleto() %> — <%= e.getNombreArea() %> / <%= e.getNombreTurno() %>
                                </option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- Turno — CU3-RN02: solo turnos de 8 horas --%>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Turno <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurno" required>
                            <option value="">Seleccionar turno...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>">
                                    <%= t.getNombreTurno() %> (<%= t.getHoraInicio() %> – <%= t.getHoraFin() %>, <%= t.getHorasDuracion() %> horas)
                                </option>
                            <% } } %>
                        </select>
                        <div class="form-text">CU3-RN02: Solo se pueden asignar turnos de 8 horas.</div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Inicio <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaInicio" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Fin <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaFin" required>
                    </div>

                </div>
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=listar" class="btn btn-secondary">Regresar</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    // Validar que fecha fin no sea anterior a fecha inicio
    document.querySelector('form').addEventListener('submit', function(e) {
        const fi = document.querySelector('[name=fechaInicio]').value;
        const ff = document.querySelector('[name=fechaFin]').value;
        if (fi && ff && ff < fi) {
            e.preventDefault();
            alert('La fecha fin no puede ser anterior a la fecha inicio.');
        }
    });
</script>
</body>
</html>
