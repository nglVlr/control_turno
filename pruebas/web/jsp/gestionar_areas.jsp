<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Area> areas = (List<Area>) request.getAttribute("listaAreas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Áreas</title>
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
            <li class="breadcrumb-item active">Gestión de Áreas</li>
        </ol>
    </nav>
    <h5 class="page-title">Gestión de Áreas</h5>

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

    <div class="card shadow-sm border-0">
        <div class="table-responsive">
            <table class="table table-hover mb-0">
                <thead class="tabla-header">
                    <tr><th>#</th><th>Área</th><th>Descripción</th><th>Acciones</th></tr>
                </thead>
                <tbody>
                    <% if (areas != null) { int i = 1; for (Area a : areas) { %>
                        <tr>
                            <td><%= i++ %></td>
                            <td><strong><%= a.getNombreArea() %></strong></td>
                            <td><%= a.getDescripcion() != null ? a.getDescripcion() : "—" %></td>
                            <td>
                                <button class="btn btn-primary btn-sm"
                                    onclick="abrirEditar(<%= a.getIdArea() %>, '<%= a.getNombreArea() %>', '<%= a.getDescripcion() != null ? a.getDescripcion() : "" %>')">
                                    Editar
                                </button>
                            </td>
                        </tr>
                    <% } } %>
                </tbody>
            </table>
        </div>
    </div>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<!-- Modal Editar Área -->
<div class="modal fade" id="modalEditarArea" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Editar Área</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="editarArea">
                <input type="hidden" name="idArea" id="idAreaModal">
                <div class="modal-body">
                    <p class="fw-semibold mb-3">Área: <span id="nombreAreaModal"></span></p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Descripción</label>
                        <input type="text" class="form-control" name="descripcion"
                               id="descripcionModal" placeholder="Descripción del área" maxlength="200">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    function abrirEditar(id, nombre, descripcion) {
        document.getElementById('idAreaModal').value = id;
        document.getElementById('nombreAreaModal').textContent = nombre;
        document.getElementById('descripcionModal').value = descripcion;
        new bootstrap.Modal(document.getElementById('modalEditarArea')).show();
    }
</script>
</body>
</html>
