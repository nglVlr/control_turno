<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.SolicitudCambioTurno"%>
<%@page import="controlTurnos.modelo.SolicitudGestion"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"AdminArea".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<SolicitudCambioTurno> listaCambios = (List<SolicitudCambioTurno>) request.getAttribute("listaSolicitudesCambio");
    List<SolicitudGestion>     listaGestion = (List<SolicitudGestion>)     request.getAttribute("listaSolicitudesGestion");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Solicitudes — Admin Área</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreArea() %> &nbsp;|&nbsp; <%= sesion.getNombreTurno() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_admin_area.jsp">Inicio</a></li>
            <li class="breadcrumb-item active">Solicitudes</li>
        </ol>
    </nav>
    <h5 class="page-title">Gestión de Solicitudes — <%= sesion.getNombreArea() %> / <%= sesion.getNombreTurno() %></h5>

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

    <!-- Tabs -->
    <ul class="nav nav-tabs mb-4">
        <li class="nav-item">
            <a class="nav-link active" data-bs-toggle="tab" href="#tabNotifCambio">
                Notificaciones de Cambio de Turno
                <% if (listaCambios != null && !listaCambios.isEmpty()) { %>
                    <span class="badge bg-warning text-dark ms-1"><%= listaCambios.size() %></span>
                <% } %>
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-bs-toggle="tab" href="#tabGestiones">
                Licencias y Vacaciones
                <% if (listaGestion != null && !listaGestion.isEmpty()) { %>
                    <span class="badge bg-danger ms-1"><%= listaGestion.size() %></span>
                <% } %>
            </a>
        </li>
    </ul>

    <div class="tab-content">

        <!-- TAB: NOTIFICACIONES DE CAMBIO DE TURNO -->
        <%-- AdminArea no aprueba/rechaza cambios de turno — eso lo hace RRHH --%>
        <%-- AdminArea solo recibe notificaciones de cambios ya resueltos --%>
        <div class="tab-pane fade show active" id="tabNotifCambio">
            <div class="alert alert-info py-2 mb-3">
                <small>ℹ️ Estas son notificaciones de cambios de turno ya resueltos por RRHH que afectan a tu área y turno.</small>
            </div>
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="tabla-header">
                            <tr><th>#</th><th>Empleado</th><th>Turno Anterior</th><th>Área Origen</th><th>Turno Nuevo</th><th>Área Destino</th><th>Decisión RRHH</th><th>Observación</th><th>Acción</th></tr>
                        </thead>
                        <tbody>
                            <% if (listaCambios != null && !listaCambios.isEmpty()) {
                                int i = 1;
                                for (SolicitudCambioTurno s : listaCambios) {
                                    // Determinar si esta área es origen o destino
                                    boolean esOrigen  = s.getIdAreaOrigen()  == sesion.getIdArea()
                                                     && s.getIdTurnoInicial() == sesion.getIdTurnoDefault()
                                                     && "Pendiente".equals(s.getNotifAdminOrigen());
                                    boolean esDestino = s.getIdAreaDestino() == sesion.getIdArea()
                                                     && s.getIdTurnoNuevo()  == sesion.getIdTurnoDefault()
                                                     && "Pendiente".equals(s.getNotifAdminDestino());
                            %>
                                <tr>
                                    <td><%= i++ %></td>
                                    <td><%= s.getNombreEmpleado() %></td>
                                    <td><%= s.getNombreTurnoInicial() %></td>
                                    <td><%= s.getNombreAreaOrigen() %></td>
                                    <td><%= s.getNombreTurnoNuevo() %></td>
                                    <td><%= s.getNombreAreaDestino() %></td>
                                    <td>
                                        <% if ("Aprobado".equals(s.getEstado())) { %>
                                            <span class="badge badge-aprobado px-2 py-1 rounded">Aprobado</span>
                                        <% } else { %>
                                            <span class="badge badge-rechazado px-2 py-1 rounded">Rechazado</span>
                                        <% } %>
                                        <% if (esOrigen) { %><br><small class="text-muted">Sale de tu área</small><% } %>
                                        <% if (esDestino) { %><br><small class="text-muted">Entra a tu área</small><% } %>
                                    </td>
                                    <td><small><%= s.getObservacionRrhh() != null ? s.getObservacionRrhh() : "—" %></small></td>
                                    <td>
                                        <% if (esOrigen) { %>
                                            <form action="${pageContext.request.contextPath}/SolicitudServlet" method="post">
                                                <input type="hidden" name="accion" value="marcarVistaOrigen">
                                                <input type="hidden" name="id" value="<%= s.getIdSolicitudCt() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-secondary">Marcar vista</button>
                                            </form>
                                        <% } else if (esDestino) { %>
                                            <form action="${pageContext.request.contextPath}/SolicitudServlet" method="post">
                                                <input type="hidden" name="accion" value="marcarVistaDestino">
                                                <input type="hidden" name="id" value="<%= s.getIdSolicitudCt() %>">
                                                <button type="submit" class="btn btn-sm btn-outline-secondary">Marcar vista</button>
                                            </form>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } } else { %>
                                <tr><td colspan="9" class="text-center text-muted py-4">No hay notificaciones pendientes.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- TAB: LICENCIAS Y VACACIONES -->
        <div class="tab-pane fade" id="tabGestiones">
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead class="tabla-header">
                            <tr><th>#</th><th>Empleado</th><th>Tipo</th><th>Fecha Inicio</th><th>Fecha Fin</th><th>Motivo</th><th>Acciones</th></tr>
                        </thead>
                        <tbody>
                            <% if (listaGestion != null && !listaGestion.isEmpty()) {
                                int i = 1;
                                for (SolicitudGestion s : listaGestion) { %>
                                <tr>
                                    <td><%= i++ %></td>
                                    <td><%= s.getNombreEmpleado() %></td>
                                    <td><%= s.getNombreTipoGestion() %></td>
                                    <td><%= s.getFechaInicio() %></td>
                                    <td><%= s.getFechaFin() %></td>
                                    <td><small><%= s.getMotivo() != null ? s.getMotivo() : "—" %></small></td>
                                    <td>
                                        <button class="btn btn-success btn-sm mb-1"
                                            onclick="abrirResolver(<%= s.getIdSolicitud() %>, 'Aprobar')">
                                            Aprobar
                                        </button>
                                        <button class="btn btn-danger btn-sm"
                                            onclick="abrirResolver(<%= s.getIdSolicitud() %>, 'Rechazar')">
                                            Rechazar
                                        </button>
                                    </td>
                                </tr>
                            <% } } else { %>
                                <tr><td colspan="7" class="text-center text-muted py-4">No hay solicitudes pendientes de tu área.</td></tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_area.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<!-- Modal Resolver Gestión -->
<div class="modal fade" id="modalResolver" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="tituloModal">Confirmar Acción</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/SolicitudServlet" method="post">
                <input type="hidden" name="accion"      value="resolverGestion">
                <input type="hidden" name="idSolicitud" id="idSolicitudModal">
                <input type="hidden" name="decision"    id="decisionModal">
                <div class="modal-body">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Observación (opcional)</label>
                        <textarea class="form-control" name="observacion" rows="3"
                                  placeholder="Ingresa una observación..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" id="btnConfirmar" class="btn btn-primary">Confirmar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    function abrirResolver(id, decision) {
        document.getElementById('idSolicitudModal').value = id;
        document.getElementById('decisionModal').value    = decision;
        document.getElementById('tituloModal').textContent = decision === 'Aprobar' ? 'Aprobar Solicitud' : 'Rechazar Solicitud';
        document.getElementById('btnConfirmar').className = 'btn ' + (decision === 'Aprobar' ? 'btn-success' : 'btn-danger');
        document.getElementById('btnConfirmar').textContent = decision === 'Aprobar' ? 'Confirmar Aprobación' : 'Confirmar Rechazo';
        new bootstrap.Modal(document.getElementById('modalResolver')).show();
    }

    // Polling cada 15 segundos — tiempo real entre pestañas
    setInterval(function() {
        fetch('${pageContext.request.contextPath}/SolicitudServlet?accion=poll')
            .then(r => r.json())
            .then(data => { if (data.pendientes > 0) location.reload(); });
    }, 15000);
</script>
</body>
</html>
