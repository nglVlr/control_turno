<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Area>  areas  = (List<Area>)  request.getAttribute("listaAreas");
    List<Turno> turnos = (List<Turno>) request.getAttribute("listaTurnos");
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
            &nbsp;|&nbsp; AdminRRHH
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

    <div class="card shadow-sm border-0" style="max-width:750px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="agregar">
                <div class="row g-3">

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">No. DPI <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="dpi" maxlength="20" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Nombre Completo <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="nombreCompleto" maxlength="150" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Usuario <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="usuario" maxlength="50" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Correo <span class="text-danger">*</span></label>
                        <input type="email" class="form-control" name="correo" maxlength="120" required>
                    </div>

                    <%-- Área --%>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idArea" id="selArea" required onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (areas != null) { for (Area a : areas) { %>
                                <option value="<%= a.getIdArea() %>"><%= a.getNombreArea() %></option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- Turno --%>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Turno <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurno" id="selTurno" required onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>"><%= t.getNombreTurno() %></option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- AdminArea — se carga dinámicamente según área y turno --%>
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Admin Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idAdminArea" id="selAdmin" required>
                            <option value="">Selecciona área y turno primero...</option>
                        </select>
                        <div class="form-text" id="msgAdmin"></div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Contraseña <span class="text-danger">*</span></label>
                        <input type="password" class="form-control" name="contrasena" maxlength="255" required>
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
<script>
    // Carga AdminAreas disponibles según área y turno seleccionados
    function cargarAdmins() {
        const idArea  = document.getElementById('selArea').value;
        const idTurno = document.getElementById('selTurno').value;
        const selAdmin = document.getElementById('selAdmin');
        const msgAdmin = document.getElementById('msgAdmin');

        if (!idArea || !idTurno) {
            selAdmin.innerHTML = '<option value="">Selecciona área y turno primero...</option>';
            return;
        }

        selAdmin.innerHTML = '<option value="">Cargando...</option>';

        fetch('${pageContext.request.contextPath}/EmpleadoServlet?accion=adminAreasPorAreaYTurno&idArea=' + idArea + '&idTurno=' + idTurno)
            .then(r => r.text())
            .then(html => {
                selAdmin.innerHTML = html;
                if (selAdmin.options.length <= 1) {
                    msgAdmin.textContent = '⚠ No hay AdminArea asignado para esta área y turno. Asigna uno primero desde Gestión de Roles.';
                    msgAdmin.className = 'form-text text-warning';
                } else {
                    msgAdmin.textContent = '';
                }
            });
    }
</script>
</body>
</html>
