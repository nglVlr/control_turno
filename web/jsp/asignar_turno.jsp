<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.util.AppConfig"%>
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
    List<Empleado> empleados  = (List<Empleado>) request.getAttribute("listaEmpleados");
    // Para AdminArea: turno fijo (el suyo). Para RRHH: lista de turnos
    Turno          turnoAdmin = (Turno)          request.getAttribute("turnoAdmin");
    List<Turno>    turnos     = (List<Turno>)    request.getAttribute("listaTurnos");
    boolean esAdminArea = "AdminArea".equals(rol);
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
    <span class="navbar-brand text-white fw-bold">Control de Turnos <span class="badge bg-secondary ms-1" style="font-size:0.65rem;vertical-align:middle;"><%=AppConfig.VERSION%></span></span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreRol() %>
            <% if (esAdminArea) { %>&nbsp;|&nbsp; <%= sesion.getNombreArea() %><% } %>
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
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=listar">Asignaciones</a>
            </li>
            <li class="breadcrumb-item active">Asignar Turno</li>
        </ol>
    </nav>
    <h5 class="page-title">Asignar Turno</h5>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <%-- Nota para AdminArea: solo puede asignar su propio turno --%>
    <% if (esAdminArea && turnoAdmin != null) { %>
        <div class="alert alert-info py-2 mb-3">
            <small>
                📋 Solo puedes asignar a <strong>tus empleados</strong> en el turno
                <strong><%= turnoAdmin.getNombreTurno() %></strong>
                (<%= turnoAdmin.getHoraInicio() %> – <%= turnoAdmin.getHoraFin() %>).
            </small>
        </div>
    <% } %>

    <div class="card shadow-sm border-0" style="max-width:600px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/AsignacionTurnoServlet" method="post">
                <input type="hidden" name="accion" value="asignar">

                <%-- Si es AdminArea el turno va oculto forzado al suyo --%>
                <% if (esAdminArea && turnoAdmin != null) { %>
                    <input type="hidden" name="idTurno" value="<%= turnoAdmin.getIdTurno() %>">
                <% } %>

                <div class="row g-3">

                    <%-- Empleados --%>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Empleado <span class="text-danger">*</span></label>
                        <select class="form-select" name="idEmpleado" required>
                            <option value="">Seleccionar empleado...</option>
                            <% if (empleados != null) { for (Empleado e : empleados) { %>
                                <option value="<%= e.getIdEmpleado() %>">
                                    <%= e.getNombreCompleto() %>
                                    — <%= e.getNombreArea() %> / <%= e.getNombreTurno() %>
                                </option>
                            <% } } %>
                        </select>
                        <% if (empleados == null || empleados.isEmpty()) { %>
                            <div class="form-text text-warning">
                                ⚠ No tienes empleados asignados. Solicita al AdminRRHH que te asigne empleados.
                            </div>
                        <% } %>
                    </div>

                    <%-- Turno: fijo para AdminArea, combo para RRHH --%>
                    <% if (!esAdminArea) { %>
                        <div class="col-md-12">
                            <label class="form-label fw-semibold">Turno <span class="text-danger">*</span></label>
                            <select class="form-select" name="idTurno" required>
                                <option value="">Seleccionar turno...</option>
                                <% if (turnos != null) { for (Turno t : turnos) { %>
                                    <option value="<%= t.getIdTurno() %>">
                                        <%= t.getNombreTurno() %>
                                        (<%= t.getHoraInicio() %> – <%= t.getHoraFin() %>)
                                    </option>
                                <% } } %>
                            </select>
                        </div>
                    <% } else if (turnoAdmin != null) { %>
                        <div class="col-md-12">
                            <label class="form-label fw-semibold">Turno</label>
                            <input type="text" class="form-control"
                                   value="<%= turnoAdmin.getNombreTurno() %> (<%= turnoAdmin.getHoraInicio() %> – <%= turnoAdmin.getHoraFin() %>)"
                                   disabled>
                            <div class="form-text">El turno asignado siempre corresponde al tuyo.</div>
                        </div>
                    <% } %>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Inicio <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaInicio" id="fechaInicio" required>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Fin <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaFin" id="fechaFin" required>
                    </div>

                </div>
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <a href="${pageContext.request.contextPath}/AsignacionTurnoServlet?accion=listar"
                       class="btn btn-secondary">Regresar</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fechaInicio').min = hoy;
    document.getElementById('fechaFin').min    = hoy;

    document.getElementById('fechaInicio').addEventListener('change', function() {
        document.getElementById('fechaFin').min = this.value || hoy;
        const ff = document.getElementById('fechaFin');
        if (ff.value && ff.value < this.value) ff.value = '';
    });

    document.querySelector('form').addEventListener('submit', function(e) {
        const fi = document.getElementById('fechaInicio').value;
        const ff = document.getElementById('fechaFin').value;
        if (fi < hoy) {
            e.preventDefault();
            alert('La fecha de inicio no puede ser un día pasado.');
            return;
        }
        if (ff && ff < fi) {
            e.preventDefault();
            alert('La fecha fin no puede ser anterior a la fecha inicio.');
        }
    });
</script>
</body>
</html>
