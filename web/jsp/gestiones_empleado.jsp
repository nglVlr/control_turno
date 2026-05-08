<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.SolicitudGestion"%>
<%@page import="controlTurnos.modelo.SolicitudCambioTurno"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"Empleado".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<SolicitudGestion>     listaGestiones = (List<SolicitudGestion>)     request.getAttribute("listaGestiones");
    List<SolicitudCambioTurno> listaCambios   = (List<SolicitudCambioTurno>) request.getAttribute("listaCambios");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mis Gestiones</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreArea() %>
            &nbsp;|&nbsp; <%= sesion.getNombreTurno() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_empleado.jsp">Inicio</a></li>
            <li class="breadcrumb-item active">Mis Gestiones</li>
        </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="page-title mb-0">Mis Gestiones</h5>
        <div class="d-flex gap-2">
            <a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=formularioGestion"
               class="btn btn-primary btn-sm">+ Nueva Gestión</a>
            <a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=formularioCambioTurno"
               class="btn btn-outline-primary btn-sm">↔ Cambio de Turno</a>
        </div>
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

    <ul class="nav nav-tabs mb-4">
        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#tabGestiones">Licencias y Permisos</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tabCambios">Cambios de Turno</a></li>
    </ul>

    <div class="tab-content">
        <!-- TAB: GESTIONES -->
        <div class="tab-pane fade show active" id="tabGestiones">
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="tabla-header">
                            <tr><th>#</th><th>Tipo</th><th>Fecha Inicio</th><th>Fecha Fin</th><th>Motivo</th><th>Estado</th></tr>
                        </thead>
                        <tbody>
                            <% if (listaGestiones != null && !listaGestiones.isEmpty()) {
                                int i = 1;
                                for (SolicitudGestion s : listaGestiones) { %>
                                <tr>
                                    <td><%= i++ %></td>
                                    <td><%= s.getNombreTipoGestion() %></td>
                                    <td><%= s.getFechaInicio() %></td>
                                    <td><%= s.getFechaFin() %></td>
                                    <td><small><%= s.getMotivo() != null ? s.getMotivo() : "—" %></small></td>
                                    <td><%= badgeEstado(s.getEstado()) %></td>
                                </tr>
                            <% } } else { %>
                                <tr><td colspan="6" class="text-center text-muted py-4">No has enviado gestiones aún.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TAB: CAMBIOS DE TURNO -->
        <div class="tab-pane fade" id="tabCambios">
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="tabla-header">
                            <tr><th>#</th><th>Turno Actual</th><th>Turno Solicitado</th><th>Área Destino</th><th>Justificación</th><th>Estado</th><th>Observación RRHH</th></tr>
                        </thead>
                        <tbody>
                            <% if (listaCambios != null && !listaCambios.isEmpty()) {
                                int i = 1;
                                for (SolicitudCambioTurno s : listaCambios) { %>
                                <tr>
                                    <td><%= i++ %></td>
                                    <td><%= s.getNombreTurnoInicial() %></td>
                                    <td><%= s.getNombreTurnoNuevo() %></td>
                                    <td><%= s.getNombreAreaDestino() %></td>
                                    <td><small><%= s.getJustificacion() != null ? s.getJustificacion() : "—" %></small></td>
                                    <td><%= badgeEstadoCt(s.getEstado()) %></td>
                                    <td><small><%= s.getObservacionRrhh() != null ? s.getObservacionRrhh() : "—" %></small></td>
                                </tr>
                            <% } } else { %>
                                <tr><td colspan="7" class="text-center text-muted py-4">No has solicitado cambios de turno aún.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_empleado.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<%!
    // Método para generar badge según estado de gestión
    private String badgeEstado(String estado) {
        if (estado == null) return "";
        switch (estado) {
            case "Pendiente":           return "<span class='badge badge-pendiente px-2 py-1 rounded'>Pendiente</span>";
            case "Aprobada AdminArea":  return "<span class='badge badge-vigente px-2 py-1 rounded'>Aprobada (área)</span>";
            case "Aprobada RRHH":       return "<span class='badge badge-aprobado px-2 py-1 rounded'>Aprobada</span>";
            case "Rechazada AdminArea": return "<span class='badge badge-rechazado px-2 py-1 rounded'>Rechazada</span>";
            case "Rechazada RRHH":      return "<span class='badge badge-rechazado px-2 py-1 rounded'>Rechazada</span>";
            default: return "<span class='badge badge-pendiente px-2 py-1 rounded'>" + estado + "</span>";
        }
    }
    private String badgeEstadoCt(String estado) {
        if (estado == null) return "";
        switch (estado) {
            case "Pendiente":  return "<span class='badge badge-pendiente px-2 py-1 rounded'>Pendiente</span>";
            case "Aprobado":   return "<span class='badge badge-aprobado px-2 py-1 rounded'>Aprobado</span>";
            case "Rechazado":  return "<span class='badge badge-rechazado px-2 py-1 rounded'>Rechazado</span>";
            default: return "<span class='badge badge-pendiente px-2 py-1 rounded'>" + estado + "</span>";
        }
    }
%>
</body>
</html>
