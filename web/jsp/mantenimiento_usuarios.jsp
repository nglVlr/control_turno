<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.util.AppConfig"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Area"%>
<%@page import="controlTurnos.modelo.Turno"%>
<%@page import="controlTurnos.modelo.Rol"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !sesion.getNombreRol().equals("AdminRRHH")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    List<Empleado> listaEmp   = (List<Empleado>) request.getAttribute("listaEmpleados");
    List<Area>     listaAreas = (List<Area>)     request.getAttribute("listaAreas");
    List<Turno>    listaTurnos= (List<Turno>)    request.getAttribute("listaTurnos");
    List<Rol>      listaRoles = (List<Rol>)      request.getAttribute("listaRoles");
    String tabActiva = request.getAttribute("tab") != null
            ? (String) request.getAttribute("tab") : "usuarios";

    // Contadores para las cards de estadísticas
    int totalActivos   = 0, totalInactivos = 0;
    int totalEmpleados = 0, totalAdmins    = 0;
    if (listaEmp != null) {
        for (Empleado e : listaEmp) {
            if ("Activo".equals(e.getEstado()))   totalActivos++;
            else                                   totalInactivos++;
            if (e.getIdRol() == 3)                 totalEmpleados++;
            else if (e.getIdRol() == 2)            totalAdmins++;
        }
    }
    int totalUsuarios = listaEmp != null ? listaEmp.size() : 0;
    int totalAreas    = listaAreas != null ? listaAreas.size() : 0;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mantenimiento de Usuarios</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .stat-card {
            border-radius: 12px;
            border: none;
            padding: 20px 24px;
            color: #fff;
            position: relative;
            overflow: hidden;
            transition: transform .15s;
        }
        .stat-card:hover { transform: translateY(-2px); }
        .stat-card .stat-icon {
            position: absolute; right: 20px; top: 50%;
            transform: translateY(-50%);
            font-size: 2.8rem; opacity: .25;
        }
        .stat-card .stat-num { font-size: 2rem; font-weight: 700; }
        .stat-card .stat-label { font-size: .8rem; opacity: .85; margin-top: 2px; }
        .bg-stat-blue   { background: linear-gradient(135deg,#2C3E50,#3498DB); }
        .bg-stat-green  { background: linear-gradient(135deg,#1a6b3a,#27ae60); }
        .bg-stat-red    { background: linear-gradient(135deg,#922b21,#e74c3c); }
        .bg-stat-purple { background: linear-gradient(135deg,#6c3483,#9b59b6); }
        .bg-stat-orange { background: linear-gradient(135deg,#935116,#e67e22); }

        .nav-tabs-custom .nav-link {
            color: #64748b; border: none; border-bottom: 3px solid transparent;
            padding: .6rem 1.2rem; font-weight: 600; font-size: .875rem;
            border-radius: 0; background: transparent;
        }
        .nav-tabs-custom .nav-link.active {
            color: #2C3E50; border-bottom-color: #3498DB; background: transparent;
        }
        .nav-tabs-custom .nav-link:hover:not(.active) {
            color: #2C3E50; border-bottom-color: #cbd5e1;
        }
        .nav-tabs-custom { border-bottom: 2px solid #e2e8f0; margin-bottom: 1.2rem; }

        .badge-rol-rrhh  { background:#1a6b3a; color:#fff; }
        .badge-rol-admin { background:#935116; color:#fff; }
        .badge-rol-emp   { background:#2C3E50; color:#fff; }
    </style>
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos
        <span class="badge bg-secondary ms-1" style="font-size:.65rem;vertical-align:middle;"><%=AppConfig.VERSION%></span>
    </span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; AdminRRHH
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container-fluid px-4 mt-4">
    <nav aria-label="breadcrumb" class="mb-3">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp">Inicio</a></li>
            <li class="breadcrumb-item active">Mantenimiento de Usuarios</li>
        </ol>
    </nav>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <h5 class="page-title mb-0">Mantenimiento de Usuarios</h5>
    </div>

    <%-- Alertas --%>
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

    <%-- CARDS DE ESTADÍSTICAS --%>
    <div class="row g-3 mb-4">
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-blue shadow-sm">
                <div class="stat-num"><%= totalUsuarios %></div>
                <div class="stat-label">Total Usuarios</div>
                <span class="stat-icon">👥</span>
            </div>
        </div>
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-green shadow-sm">
                <div class="stat-num"><%= totalActivos %></div>
                <div class="stat-label">Activos</div>
                <span class="stat-icon">✅</span>
            </div>
        </div>
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-red shadow-sm">
                <div class="stat-num"><%= totalInactivos %></div>
                <div class="stat-label">Inactivos</div>
                <span class="stat-icon">🚫</span>
            </div>
        </div>
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-purple shadow-sm">
                <div class="stat-num"><%= totalAdmins %></div>
                <div class="stat-label">AdminArea</div>
                <span class="stat-icon">🏢</span>
            </div>
        </div>
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-orange shadow-sm">
                <div class="stat-num"><%= totalEmpleados %></div>
                <div class="stat-label">Empleados</div>
                <span class="stat-icon">👤</span>
            </div>
        </div>
        <div class="col-md-2 col-sm-4 col-6">
            <div class="stat-card bg-stat-blue shadow-sm">
                <div class="stat-num"><%= totalAreas %></div>
                <div class="stat-label">Áreas</div>
                <span class="stat-icon">📍</span>
            </div>
        </div>
    </div>

    <%-- TABS DE NAVEGACIÓN --%>
    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <ul class="nav nav-tabs-custom px-3 pt-3" id="mainTabs">
                <li class="nav-item">
                    <a class="nav-link <%= "usuarios".equals(tabActiva) ? "active" : "" %>"
                       href="#tabUsuarios" data-bs-toggle="tab">
                        👥 Usuarios
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "agregar".equals(tabActiva) ? "active" : "" %>"
                       href="#tabAgregar" data-bs-toggle="tab">
                        ➕ Agregar Usuario
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "roles".equals(tabActiva) ? "active" : "" %>"
                       href="#tabRoles" data-bs-toggle="tab">
                        🎭 Gestión de Roles
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link <%= "areas".equals(tabActiva) ? "active" : "" %>"
                       href="#tabAreas" data-bs-toggle="tab">
                        📍 Gestión de Áreas
                    </a>
                </li>
            </ul>

            <div class="tab-content px-3 pb-3">

                <%-- ══════════════ TAB: USUARIOS ══════════════ --%>
                <div class="tab-pane fade <%= "usuarios".equals(tabActiva) ? "show active" : "" %>" id="tabUsuarios">
                    <div class="d-flex justify-content-between align-items-center mb-2 mt-2">
                        <input type="text" id="filtroUsuarios" class="form-control form-control-sm"
                               style="max-width:320px;" placeholder="Buscar usuario...">
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="tablaUsuarios">
                            <thead class="tabla-header">
                                <tr>
                                    <th>#</th><th>Nombre</th><th>Usuario</th><th>Área</th>
                                    <th>Turno</th><th>Rol</th><th>Admin Área</th>
                                    <th>Estado</th><th>Creado por</th><th>Modificado por</th><th>Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (listaEmp != null && !listaEmp.isEmpty()) {
                                    int i = 1;
                                    for (Empleado e : listaEmp) { %>
                                    <tr>
                                        <td><%= i++ %></td>
                                        <td><strong><%= e.getNombreCompleto() %></strong></td>
                                        <td><code><%= e.getUsuario() %></code></td>
                                        <td><%= e.getNombreArea() != null ? e.getNombreArea() : "—" %></td>
                                        <td><%= e.getNombreTurno() != null ? e.getNombreTurno() : "—" %></td>
                                        <td>
                                            <%
                                                String badgeCls = e.getIdRol()==1 ? "badge-rol-rrhh"
                                                                : e.getIdRol()==2 ? "badge-rol-admin"
                                                                : "badge-rol-emp";
                                            %>
                                            <span class="badge <%= badgeCls %> px-2 py-1 rounded">
                                                <%= e.getNombreRol() %>
                                            </span>
                                        </td>
                                        <td><small><%= e.getNombreAdminArea() != null ? e.getNombreAdminArea() : "—" %></small></td>
                                        <td>
                                            <% if ("Activo".equals(e.getEstado())) { %>
                                                <span class="badge badge-activo px-2 py-1 rounded">Activo</span>
                                            <% } else { %>
                                                <span class="badge badge-inactivo px-2 py-1 rounded">Inactivo</span>
                                            <% } %>
                                        </td>
                                        <td><small><%= e.getCreadoPorNombre() != null ? e.getCreadoPorNombre() : "—" %></small></td>
                                        <td><small><%= e.getModificadoPorNombre() != null ? e.getModificadoPorNombre() : "—" %></small></td>
                                        <td>
                                            <% if ("Activo".equals(e.getEstado())) { %>
                                                <button class="btn btn-danger btn-sm"
                                                    onclick="abrirInactivar(<%= e.getIdEmpleado() %>, '<%= e.getNombreCompleto().replace("'","") %>')">
                                                    Inactivar
                                                </button>
                                            <% } else { %>
                                                <button class="btn btn-success btn-sm"
                                                    onclick="confirmarReactivar(<%= e.getIdEmpleado() %>, '<%= e.getNombreCompleto().replace("'","") %>')">
                                                    Reactivar
                                                </button>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } } else { %>
                                    <tr><td colspan="11" class="text-center text-muted py-4">No hay usuarios registrados.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <%-- ══════════════ TAB: AGREGAR USUARIO ══════════════ --%>
                <div class="tab-pane fade <%= "agregar".equals(tabActiva) ? "show active" : "" %>" id="tabAgregar">
                    <div class="row justify-content-start mt-2">
                        <div class="col-lg-8">
                            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post" id="formAgregar">
                                <input type="hidden" name="accion" value="agregar">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">No. DPI <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" name="dpi" id="dpi"
                                               maxlength="15" placeholder="1234 56789 1234"
                                               pattern="\d{4}\s\d{5}\s\d{4}"
                                               title="Formato: 1234 56789 1234" required>
                                        <div class="form-text">Formato Guatemala: 4 dígitos espacio 5 dígitos espacio 4 dígitos</div>
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
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Contraseña <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" name="contrasena" maxlength="255" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Rol <span class="text-danger">*</span></label>
                                        <select class="form-select" name="idRol" id="selRol" required onchange="actualizarFormulario()">
                                            <option value="">Seleccionar rol...</option>
                                            <% if (listaRoles != null) { for (Rol r : listaRoles) { %>
                                                <option value="<%= r.getIdRol() %>"><%= r.getNombreRol() %></option>
                                            <% } } %>
                                        </select>
                                    </div>
                                    <div class="col-md-4" id="bloqueArea">
                                        <label class="form-label fw-semibold">Área</label>
                                        <select class="form-select" name="idArea" id="selArea" onchange="cargarAdmins()">
                                            <option value="">Seleccionar...</option>
                                            <% if (listaAreas != null) { for (Area a : listaAreas) { %>
                                                <option value="<%= a.getIdArea() %>"><%= a.getNombreArea() %></option>
                                            <% } } %>
                                        </select>
                                    </div>
                                    <div class="col-md-4" id="bloqueTurno">
                                        <label class="form-label fw-semibold">Turno</label>
                                        <select class="form-select" name="idTurno" id="selTurno" onchange="cargarAdmins()">
                                            <option value="">Seleccionar...</option>
                                            <% if (listaTurnos != null) { for (Turno t : listaTurnos) { %>
                                                <option value="<%= t.getIdTurno() %>"><%= t.getNombreTurno() %></option>
                                            <% } } %>
                                        </select>
                                    </div>
                                    <div class="col-md-4" id="bloqueAdmin" style="display:none;">
                                        <label class="form-label fw-semibold">Admin Área <span class="text-danger">*</span></label>
                                        <select class="form-select" name="idAdminArea" id="selAdmin">
                                            <option value="">Selecciona área y turno...</option>
                                        </select>
                                        <div class="form-text" id="msgAdmin"></div>
                                    </div>
                                    <input type="hidden" name="idAdminAreaHidden" id="idAdminAreaHidden" value="0">
                                    <div id="notaRol" class="col-12" style="display:none;">
                                        <div class="alert alert-info py-2 mb-0 small" id="notaRolMsg"></div>
                                    </div>
                                </div>
                                <div class="d-flex gap-2 mt-3">
                                    <button type="submit" class="btn btn-primary">Registrar</button>
                                    <button type="reset" class="btn btn-outline-secondary" onclick="resetForm()">Limpiar</button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <%-- ══════════════ TAB: GESTIÓN DE ROLES ══════════════ --%>
                <div class="tab-pane fade <%= "roles".equals(tabActiva) ? "show active" : "" %>" id="tabRoles">
                    <div class="alert alert-info py-2 mb-2 mt-2 small">
                        ℹ️ Pueden existir <strong>múltiples AdminArea</strong> por área y turno, cada uno con sus propios empleados asignados.
                    </div>
                    <div class="mb-2">
                        <input type="text" id="filtroRoles" class="form-control form-control-sm"
                               style="max-width:320px;" placeholder="Buscar...">
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="tablaRoles">
                            <thead class="tabla-header">
                                <tr><th>#</th><th>Nombre</th><th>Usuario</th><th>Área</th><th>Turno</th><th>Rol Actual</th><th>Estado</th><th>Cambiar Rol</th></tr>
                            </thead>
                            <tbody>
                                <% if (listaEmp != null && !listaEmp.isEmpty()) {
                                    int i = 1;
                                    for (Empleado e : listaEmp) { %>
                                    <tr>
                                        <td><%= i++ %></td>
                                        <td><strong><%= e.getNombreCompleto() %></strong></td>
                                        <td><code><%= e.getUsuario() %></code></td>
                                        <td><%= e.getNombreArea() != null ? e.getNombreArea() : "—" %></td>
                                        <td><%= e.getNombreTurno() != null ? e.getNombreTurno() : "—" %></td>
                                        <td>
                                            <%
                                                String bc = e.getIdRol()==1 ? "badge-rol-rrhh"
                                                          : e.getIdRol()==2 ? "badge-rol-admin"
                                                          : "badge-rol-emp";
                                            %>
                                            <span class="badge <%= bc %> px-2 py-1 rounded"><%= e.getNombreRol() %></span>
                                        </td>
                                        <td>
                                            <% if ("Activo".equals(e.getEstado())) { %>
                                                <span class="badge badge-activo px-2 py-1 rounded">Activo</span>
                                            <% } else { %>
                                                <span class="badge badge-inactivo px-2 py-1 rounded">Inactivo</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if ("Activo".equals(e.getEstado())) { %>
                                                <button class="btn btn-primary btn-sm"
                                                    onclick="abrirCambiarRol(<%= e.getIdEmpleado() %>,
                                                        '<%= e.getNombreCompleto().replace("'","") %>',
                                                        '<%= e.getNombreArea() != null ? e.getNombreArea() : "" %>',
                                                        '<%= e.getNombreTurno() != null ? e.getNombreTurno() : "Sin turno" %>',
                                                        <%= e.getIdRol() %>)">
                                                    Cambiar Rol
                                                </button>
                                            <% } else { %>
                                                <span class="text-muted small">—</span>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } } else { %>
                                    <tr><td colspan="8" class="text-center text-muted py-4">No hay usuarios.</td></tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <%-- ══════════════ TAB: GESTIÓN DE ÁREAS ══════════════ --%>
                <div class="tab-pane fade <%= "areas".equals(tabActiva) ? "show active" : "" %>" id="tabAreas">
                    <div class="table-responsive mt-2">
                        <table class="table table-hover mb-0">
                            <thead class="tabla-header">
                                <tr><th>#</th><th>Área</th><th>Descripción</th><th>Acciones</th></tr>
                            </thead>
                            <tbody>
                                <% if (listaAreas != null) { int i = 1; for (Area a : listaAreas) { %>
                                    <tr>
                                        <td><%= i++ %></td>
                                        <td><strong><%= a.getNombreArea() %></strong></td>
                                        <td><%= a.getDescripcion() != null ? a.getDescripcion() : "—" %></td>
                                        <td>
                                            <button class="btn btn-primary btn-sm"
                                                onclick="abrirEditarArea(<%= a.getIdArea() %>,
                                                    '<%= a.getNombreArea().replace("'","") %>',
                                                    '<%= a.getDescripcion() != null ? a.getDescripcion().replace("'","") : "" %>')">
                                                Editar
                                            </button>
                                        </td>
                                    </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </div>

            </div><%-- fin tab-content --%>
        </div>
    </div>

    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/jsp/menu_admin_rrhh.jsp" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<%-- ══════════════ MODALES ══════════════ --%>

<%-- Modal Inactivar --%>
<div class="modal fade" id="modalInactivar" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Inactivar Empleado</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="inactivar">
                <input type="hidden" name="idEmpleado" id="idInactivarModal">
                <div class="modal-body">
                    <p>¿Inactivar a <strong id="nombreInactivarModal"></strong>?</p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Motivo <span class="text-danger">*</span></label>
                        <select class="form-select" name="motivoInactivacion" required>
                            <option value="">Seleccionar...</option>
                            <option>Permiso Personal</option>
                            <option>Vacaciones</option>
                            <option>Cita al IGSS</option>
                            <option>Licencia de Cumpleanos</option>
                            <option>Suspension Laboral</option>
                            <option>Otros</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-danger">Inactivar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Modal Reactivar --%>
<div class="modal fade" id="modalReactivar" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Reactivar Empleado</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="reactivar">
                <input type="hidden" name="idEmpleado" id="idReactivarModal">
                <div class="modal-body">
                    <div class="alert alert-success py-2">
                        ¿Reactivar a <strong id="nombreReactivarModal"></strong>?
                        El empleado podrá iniciar sesión nuevamente.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-success">Reactivar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Modal Cambiar Rol --%>
<div class="modal fade" id="modalCambiarRol" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Cambiar Rol</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post">
                <input type="hidden" name="accion" value="cambiarRol">
                <input type="hidden" name="idEmpleado" id="idRolModal">
                <div class="modal-body">
                    <p class="mb-1">Empleado: <strong id="nombreRolModal"></strong></p>
                    <p class="text-muted small mb-3">Área: <span id="areaRolModal"></span> | Turno: <span id="turnoRolModal"></span></p>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Nuevo Rol <span class="text-danger">*</span></label>
                        <select class="form-select" name="idRol" id="selectRolModal" required>
                            <option value="">Seleccionar...</option>
                            <% if (listaRoles != null) { for (Rol r : listaRoles) { %>
                                <option value="<%= r.getIdRol() %>"><%= r.getNombreRol() %></option>
                            <% } } %>
                        </select>
                    </div>
                    <div class="alert alert-warning py-2 small" id="alertaAdminArea" style="display:none;">
                        ⚠️ Si ya existe un AdminArea en esta área y turno, será degradado automáticamente.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Confirmar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- Modal Editar Área --%>
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
                               id="descripcionModal" maxlength="200">
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
// ── Filtros en tiempo real ──────────────────────────────────
document.getElementById('filtroUsuarios').addEventListener('keyup', function() {
    filtrarTabla('tablaUsuarios', this.value);
});
document.getElementById('filtroRoles').addEventListener('keyup', function() {
    filtrarTabla('tablaRoles', this.value);
});
function filtrarTabla(idTabla, texto) {
    texto = texto.toLowerCase();
    document.querySelectorAll('#' + idTabla + ' tbody tr').forEach(f => {
        f.style.display = f.textContent.toLowerCase().includes(texto) ? '' : 'none';
    });
}

// ── DPI auto-formato ────────────────────────────────────────
document.getElementById('dpi').addEventListener('input', function() {
    let val = this.value.replace(/\D/g,'');
    if (val.length > 13) val = val.substring(0,13);
    let fmt = val.length <= 4 ? val
            : val.length <= 9 ? val.substring(0,4)+' '+val.substring(4)
            : val.substring(0,4)+' '+val.substring(4,9)+' '+val.substring(9);
    this.value = fmt;
    this.classList.toggle('is-valid',   val.length === 13);
    this.classList.toggle('is-invalid', val.length > 0 && val.length !== 13);
});

// ── Lógica rol en formulario agregar ────────────────────────
function actualizarFormulario() {
    const rol = document.getElementById('selRol').value;
    const notaDiv = document.getElementById('notaRol');
    const notaMsg = document.getElementById('notaRolMsg');
    const bloqAdmin = document.getElementById('bloqueAdmin');
    const selArea   = document.getElementById('selArea');
    const selTurno  = document.getElementById('selTurno');
    document.getElementById('selAdmin').removeAttribute('required');
    bloqAdmin.style.display = 'none';
    selArea.removeAttribute('required');
    selTurno.removeAttribute('required');
    notaDiv.style.display = 'none';
    if (rol === '1') {
        notaMsg.innerHTML = '👑 <strong>AdminRRHH:</strong> Área y turno son opcionales. Si no seleccionas área, se asignará automáticamente a <strong>Recursos Humanos</strong>.';
        notaDiv.style.display = 'block';
    } else if (rol === '2') {
        selArea.setAttribute('required','required');
        selTurno.setAttribute('required','required');
        notaMsg.innerHTML = '🏢 <strong>AdminArea:</strong> Requiere área y turno. No depende de otro AdminArea.';
        notaDiv.style.display = 'block';
    } else if (rol === '3') {
        selArea.setAttribute('required','required');
        selTurno.setAttribute('required','required');
        bloqAdmin.style.display = 'block';
        document.getElementById('selAdmin').setAttribute('required','required');
        notaMsg.innerHTML = '👤 <strong>Empleado:</strong> Requiere área, turno y AdminArea.';
        notaDiv.style.display = 'block';
        cargarAdmins();
    }
}

function cargarAdmins() {
    if (document.getElementById('selRol').value !== '3') return;
    const idArea  = document.getElementById('selArea').value;
    const idTurno = document.getElementById('selTurno').value;
    const selAdmin = document.getElementById('selAdmin');
    const msgAdmin = document.getElementById('msgAdmin');
    if (!idArea || !idTurno) {
        selAdmin.innerHTML = '<option value="">Selecciona área y turno...</option>';
        return;
    }
    selAdmin.innerHTML = '<option value="">Cargando...</option>';
    fetch('${pageContext.request.contextPath}/EmpleadoServlet?accion=adminAreasPorAreaYTurno&idArea='+idArea+'&idTurno='+idTurno)
        .then(r => r.text()).then(html => {
            selAdmin.innerHTML = html;
            msgAdmin.textContent = selAdmin.options.length <= 1
                ? '⚠ No hay AdminArea para esta combinación.' : '';
        });
}

function resetForm() {
    document.getElementById('selRol').value = '';
    document.getElementById('bloqueAdmin').style.display = 'none';
    document.getElementById('notaRol').style.display = 'none';
    document.getElementById('dpi').classList.remove('is-valid','is-invalid');
}

// ── Submit del formulario ───────────────────────────────────
document.getElementById('formAgregar').addEventListener('submit', function(e) {
    const dpi = document.getElementById('dpi').value.replace(/\D/g,'');
    if (dpi.length !== 13) {
        e.preventDefault();
        alert('El DPI debe tener exactamente 13 dígitos. Formato: 1234 56789 1234');
        document.getElementById('dpi').focus(); return;
    }
    const rol = document.getElementById('selRol').value;
    const hidden = document.getElementById('idAdminAreaHidden');
    if (rol === '3') {
        const v = document.getElementById('selAdmin').value;
        hidden.value = v;
        if (!v) { e.preventDefault(); alert('Debes seleccionar un AdminArea.'); }
    } else { hidden.value = '0'; }
});

// ── Modales ─────────────────────────────────────────────────
function abrirInactivar(id, nombre) {
    document.getElementById('idInactivarModal').value = id;
    document.getElementById('nombreInactivarModal').textContent = nombre;
    new bootstrap.Modal(document.getElementById('modalInactivar')).show();
}
function confirmarReactivar(id, nombre) {
    document.getElementById('idReactivarModal').value = id;
    document.getElementById('nombreReactivarModal').textContent = nombre;
    new bootstrap.Modal(document.getElementById('modalReactivar')).show();
}
function abrirCambiarRol(id, nombre, area, turno, rolActual) {
    document.getElementById('idRolModal').value = id;
    document.getElementById('nombreRolModal').textContent = nombre;
    document.getElementById('areaRolModal').textContent = area;
    document.getElementById('turnoRolModal').textContent = turno;
    document.getElementById('selectRolModal').value = rolActual;
    document.getElementById('alertaAdminArea').style.display = 'none';
    new bootstrap.Modal(document.getElementById('modalCambiarRol')).show();
}
document.getElementById('selectRolModal').addEventListener('change', function() {
    document.getElementById('alertaAdminArea').style.display =
        this.value === '2' ? 'block' : 'none';
});
function abrirEditarArea(id, nombre, descripcion) {
    document.getElementById('idAreaModal').value = id;
    document.getElementById('nombreAreaModal').textContent = nombre;
    document.getElementById('descripcionModal').value = descripcion;
    new bootstrap.Modal(document.getElementById('modalEditarArea')).show();
}
</script>
</body>
</html>
