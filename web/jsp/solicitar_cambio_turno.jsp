<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="controlTurnos.dao.AreaDAO"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"Empleado".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Turno> turnos = (List<Turno>) request.getAttribute("listaTurnos");
    // Cargar áreas para el combo de área destino
    AreaDAO areaDAO = new AreaDAO();
    List<Area> areas = areaDAO.listarActivas();
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Solicitar Cambio de Turno</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreArea() %> / <%= sesion.getNombreTurno() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_empleado.jsp">Inicio</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=menu">Mis Gestiones</a></li>
            <li class="breadcrumb-item active">Cambio de Turno</li>
        </ol>
    </nav>
    <h5 class="page-title">Solicitar Cambio de Turno</h5>

    <div class="alert alert-info py-2 mb-3">
        <small>
            ℹ️ Tu turno actual es <strong><%= sesion.getNombreTurno() %></strong>
            en el área de <strong><%= sesion.getNombreArea() %></strong>.
            La solicitud será revisada por el AdminRRHH.
        </small>
    </div>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <div class="card shadow-sm border-0" style="max-width:600px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/EmpleadoGestionServlet" method="post">
                <input type="hidden" name="accion" value="crearCambioTurno">
                <div class="row g-3">

                    <%-- CU5-FA02 paso 3: fecha inicial --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Inicial <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaInicial" required>
                    </div>

                    <%-- CU5-FA02 paso 5: fecha nueva --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Nueva <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaNueva" required>
                    </div>

                    <%-- CU5-FA02 paso 6: turno nuevo --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Turno Solicitado <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurnoNuevo" required>
                            <option value="">Seleccionar...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>"
                                    <%= t.getIdTurno() == sesion.getIdTurnoDefault() ? "disabled" : "" %>>
                                    <%= t.getNombreTurno() %> (<%= t.getHoraInicio() %> - <%= t.getHoraFin() %>)
                                </option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- Área destino --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Área Destino <span class="text-danger">*</span></label>
                        <select class="form-select" name="idAreaDestino" required>
                            <option value="">Seleccionar...</option>
                            <% if (areas != null) { for (Area a : areas) { %>
                                <option value="<%= a.getIdArea() %>"
                                    <%= a.getIdArea() == sesion.getIdArea() ? "selected" : "" %>>
                                    <%= a.getNombreArea() %>
                                    <%= a.getIdArea() == sesion.getIdArea() ? "(misma área)" : "" %>
                                </option>
                            <% } } %>
                        </select>
                        <div class="form-text">Si es en la misma área, deja seleccionada tu área actual.</div>
                    </div>

                    <%-- CU5-FA02 paso 7: justificación --%>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Justificación <span class="text-danger">*</span></label>
                        <textarea class="form-control" name="justificacion" rows="3"
                                  placeholder="Explica el motivo del cambio de turno..." required></textarea>
                    </div>

                </div>
                <div class="d-flex gap-2 mt-4">
                    <button type="submit" class="btn btn-primary">Guardar</button>
                    <button type="reset" class="btn btn-outline-secondary">Limpiar</button>
                    <a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=menu"
                       class="btn btn-secondary">Regresar</a>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    document.querySelector('form').addEventListener('submit', function(e) {
        const fi = document.querySelector('[name=fechaInicial]').value;
        const fn = document.querySelector('[name=fechaNueva]').value;
        if (fi && fn && fn < fi) {
            e.preventDefault();
            alert('La fecha nueva no puede ser anterior a la fecha inicial.');
        }
    });
</script>
</body>
</html>
