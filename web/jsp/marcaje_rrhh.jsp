<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Marcaje"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"AdminRRHH".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Marcaje> lista = (List<Marcaje>) request.getAttribute("listaMarcajes");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Marcaje General</title>
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
            <li class="breadcrumb-item active">Marcaje General</li>
        </ol>
    </nav>
    <h5 class="page-title">Marcaje del Día — Vista General</h5>

    <div class="alert alert-info py-2 mb-3">
        <small>👁 Vista de solo lectura. RRHH puede monitorear el marcaje de todos los empleados.</small>
    </div>

    <div class="mb-3">
        <input type="text" id="filtro" class="form-control" style="max-width:350px;"
               placeholder="Buscar por nombre...">
    </div>

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0" id="tablaMarcajes">
                <thead class="tabla-header">
                    <tr>
                        <th>#</th><th>Empleado</th><th>Entrada</th>
                        <th>Descanso 1</th><th>Descanso 2</th><th>Salida</th><th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (lista != null && !lista.isEmpty()) {
                        int i = 1;
                        for (Marcaje m : lista) { %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><%= m.getNombreEmpleado() %></td>
                            <td><%= m.getHoraEntrada()   != null ? m.getHoraEntrada()   : "—" %></td>
                            <td><%= m.getHoraDescanso1() != null ? m.getHoraDescanso1() : "—" %></td>
                            <td><%= m.getHoraDescanso2() != null ? m.getHoraDescanso2() : "—" %></td>
                            <td><%= m.getHoraSalida()    != null ? m.getHoraSalida()    : "—" %></td>
                            <td>
                                <% if (m.getHoraSalida() != null) { %>
                                    <span class="badge badge-aprobado px-2 py-1 rounded">Completo</span>
                                <% } else if (m.getHoraEntrada() != null) { %>
                                    <span class="badge badge-vigente px-2 py-1 rounded">En turno</span>
                                    <% if (m.getEntradaTarde() == 1) { %>
                                        <span class="badge badge-rechazado px-2 py-1 rounded ms-1">Tarde</span>
                                    <% } %>
                                <% } else { %>
                                    <span class="badge badge-inactivo px-2 py-1 rounded">Sin marcar</span>
                                <% } %>
                            </td>
                        </tr>
                    <% } } else { %>
                        <tr><td colspan="7" class="text-center text-muted py-4">No hay marcajes registrados hoy.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('filtro').addEventListener('keyup', function() {
        const texto = this.value.toLowerCase();
        document.querySelectorAll('#tablaMarcajes tbody tr').forEach(f => {
            f.style.display = f.textContent.toLowerCase().includes(texto) ? '' : 'none';
        });
    });
    // Polling cada 15 segundos — actualización en tiempo real
    setInterval(() => location.reload(), 15000);
</script>
</body>
</html>
