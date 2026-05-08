<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="controlTurnos.util.AppConfig"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"AdminRRHH".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    Empleado emp    = (Empleado)    request.getAttribute("empleado");
    List<Area>  areas  = (List<Area>)  request.getAttribute("listaAreas");
    List<Turno> turnos = (List<Turno>) request.getAttribute("listaTurnos");
    if (emp == null) {
        response.sendRedirect(request.getContextPath() + "/ReasignacionServlet?accion=listar"); return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reasignar Empleado</title>
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
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/ReasignacionServlet?accion=listar">Reasignación</a></li>
            <li class="breadcrumb-item active">Reasignar Empleado</li>
        </ol>
    </nav>
    <h5 class="page-title">Reasignar Empleado</h5>

    <%-- Datos actuales del empleado --%>
    <div class="card shadow-sm border-0 mb-4" style="max-width:700px;">
        <div class="card-body p-3">
            <h6 class="fw-bold mb-2" style="color:#2C3E50;">Datos Actuales</h6>
            <div class="row g-2 text-muted small">
                <div class="col-md-4"><strong>Empleado:</strong> <%= emp.getNombreCompleto() %></div>
                <div class="col-md-4"><strong>Área:</strong> <%= emp.getNombreArea() %></div>
                <div class="col-md-4"><strong>Turno:</strong> <%= emp.getNombreTurno() != null ? emp.getNombreTurno() : "Sin turno" %></div>
                <div class="col-md-6"><strong>Admin Área:</strong> <%= emp.getNombreAdminArea() != null ? emp.getNombreAdminArea() : "Sin asignar" %></div>
            </div>
        </div>
    </div>

    <div class="card shadow-sm border-0" style="max-width:700px;">
        <div class="card-body p-4">
            <h6 class="fw-bold mb-3" style="color:#2C3E50;">Nueva Asignación</h6>
            <form action="${pageContext.request.contextPath}/ReasignacionServlet" method="post">
                <input type="hidden" name="accion"      value="reasignar">
                <input type="hidden" name="idEmpleado"  value="<%= emp.getIdEmpleado() %>">
                <div class="row g-3">

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Nueva Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idArea" id="selArea" required onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (areas != null) { for (Area a : areas) { %>
                                <option value="<%= a.getIdArea() %>"
                                    <%= a.getIdArea() == emp.getIdArea() ? "selected" : "" %>>
                                    <%= a.getNombreArea() %>
                                </option>
                            <% } } %>
                        </select>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Nuevo Turno <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurno" id="selTurno" required onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>"
                                    <%= t.getIdTurno() == emp.getIdTurnoDefault() ? "selected" : "" %>>
                                    <%= t.getNombreTurno() %> (<%= t.getHoraInicio() %> – <%= t.getHoraFin() %>)
                                </option>
                            <% } } %>
                        </select>
                    </div>

                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Nuevo Admin Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idAdminArea" id="selAdmin" required>
                            <option value="">Selecciona área y turno primero...</option>
                        </select>
                        <div class="form-text" id="msgAdmin"></div>
                    </div>

                </div>
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Guardar Reasignación</button>
                    <a href="${pageContext.request.contextPath}/ReasignacionServlet?accion=listar"
                       class="btn btn-secondary">Cancelar</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    // Cargar AdminAreas disponibles por área+turno via AJAX
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
        fetch('${pageContext.request.contextPath}/ReasignacionServlet?accion=adminAreasPorAreaYTurno&idArea=' + idArea + '&idTurno=' + idTurno)
            .then(r => r.text())
            .then(html => {
                selAdmin.innerHTML = html;
                if (selAdmin.options.length <= 1) {
                    msgAdmin.textContent = '⚠ No hay AdminArea para esta combinación. Asigna uno desde Gestión de Roles.';
                    msgAdmin.className = 'form-text text-warning';
                } else {
                    msgAdmin.textContent = '';
                }
            });
    }

    // Auto-cargar admins con los valores actuales del empleado
    window.addEventListener('load', function() {
        const idArea  = document.getElementById('selArea').value;
        const idTurno = document.getElementById('selTurno').value;
        if (idArea && idTurno) cargarAdmins();
    });
</script>
</body>
</html>
