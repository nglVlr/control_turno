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
    List<Area>  areas  = (List<Area>)  request.getAttribute("listaAreas");
    List<Turno> turnos = (List<Turno>) request.getAttribute("listaTurnos");
    List<Rol>   roles  = (List<Rol>)   request.getAttribute("listaRoles");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Agregar Usuario</title>
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
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/EmpleadoServlet?accion=listar">Usuarios</a></li>
            <li class="breadcrumb-item active">Agregar Usuario</li>
        </ol>
    </nav>
    <h5 class="page-title">Agregar Usuario</h5>

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

    <div class="card shadow-sm border-0" style="max-width:800px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/EmpleadoServlet" method="post" id="formAgregar">
                <input type="hidden" name="accion" value="agregar">
                <div class="row g-3">

                    <div class="col-md-6">
                        <label class="form-label fw-semibold">No. DPI <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" name="dpi" id="dpi"
                               maxlength="15" placeholder="1234 56789 1234"
                               pattern="\d{4}\s\d{5}\s\d{4}"
                               title="El DPI debe tener el formato: 1234 56789 1234 (13 dígitos)"
                               required>
                        <div class="form-text">Formato: 4 dígitos, espacio, 5 dígitos, espacio, 4 dígitos</div>
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

                    <%-- ROL — determina qué campos adicionales se muestran --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Rol <span class="text-danger">*</span></label>
                        <select class="form-select" name="idRol" id="selRol" required onchange="actualizarFormulario()">
                            <option value="">Seleccionar rol...</option>
                            <% if (roles != null) { for (Rol r : roles) { %>
                                <option value="<%= r.getIdRol() %>"><%= r.getNombreRol() %></option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- ÁREA — visible para todos los roles --%>
                    <div class="col-md-4" id="bloqueArea">
                        <label class="form-label fw-semibold">Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idArea" id="selArea" onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (areas != null) { for (Area a : areas) { %>
                                <option value="<%= a.getIdArea() %>"><%= a.getNombreArea() %></option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- TURNO — visible para todos los roles --%>
                    <div class="col-md-4" id="bloqueTurno">
                        <label class="form-label fw-semibold">Turno <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTurno" id="selTurno" onchange="cargarAdmins()">
                            <option value="">Seleccionar...</option>
                            <% if (turnos != null) { for (Turno t : turnos) { %>
                                <option value="<%= t.getIdTurno() %>"><%= t.getNombreTurno() %></option>
                            <% } } %>
                        </select>
                    </div>

                    <%-- ADMINAREA — solo visible si el rol es Empleado --%>
                    <div class="col-md-4" id="bloqueAdmin" style="display:none;">
                        <label class="form-label fw-semibold">Admin Área <span class="text-danger">*</span></label>
                        <select class="form-select" name="idAdminArea" id="selAdmin">
                            <option value="">Selecciona área y turno primero...</option>
                        </select>
                        <div class="form-text" id="msgAdmin"></div>
                    </div>

                    <%-- INPUT oculto para enviar idAdminArea = 0 cuando no aplica --%>
                    <input type="hidden" name="idAdminAreaHidden" id="idAdminAreaHidden" value="0">

                </div>

                <%-- Nota informativa según rol --%>
                <div id="notaRol" class="alert alert-info py-2 mt-3 small" style="display:none;"></div>

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
    // ─────────────────────────────────────────────────────────
    // Actualiza el formulario según el rol seleccionado
    // AdminRRHH : área y turno opcionales, sin AdminArea
    // AdminArea : área y turno requeridos, sin AdminArea
    // Empleado  : área, turno y AdminArea requeridos
    // ─────────────────────────────────────────────────────────
    function actualizarFormulario() {
        const rol      = document.getElementById('selRol');
        const rolVal   = rol.value;
        const rolTxt   = rol.options[rol.selectedIndex]?.text || '';
        const bloqAdmin = document.getElementById('bloqueAdmin');
        const selArea   = document.getElementById('selArea');
        const selTurno  = document.getElementById('selTurno');
        const notaRol   = document.getElementById('notaRol');
        const hiddenAdmin = document.getElementById('idAdminAreaHidden');

        // Limpiar estado
        bloqAdmin.style.display = 'none';
        document.getElementById('selAdmin').removeAttribute('required');
        selArea.removeAttribute('required');
        selTurno.removeAttribute('required');
        hiddenAdmin.value = '0';
        notaRol.style.display = 'none';

        if (rolVal === '1') {
            // AdminRRHH — área y turno opcionales, sin AdminArea
            notaRol.style.display = 'block';
            notaRol.innerHTML = '👑 <strong>AdminRRHH:</strong> El área y turno son referenciales. No necesita depender de un AdminArea.';
        } else if (rolVal === '2') {
            // AdminArea — área y turno requeridos, sin AdminArea
            selArea.setAttribute('required', 'required');
            selTurno.setAttribute('required', 'required');
            notaRol.style.display = 'block';
            notaRol.innerHTML = '🏢 <strong>AdminArea:</strong> Debe tener área y turno asignados. No depende de otro AdminArea.';
        } else if (rolVal === '3') {
            // Empleado — área, turno y AdminArea requeridos
            selArea.setAttribute('required', 'required');
            selTurno.setAttribute('required', 'required');
            bloqAdmin.style.display = 'block';
            document.getElementById('selAdmin').setAttribute('required', 'required');
            notaRol.style.display = 'block';
            notaRol.innerHTML = '👤 <strong>Empleado:</strong> Debe estar asignado a un AdminArea de su área y turno.';
            cargarAdmins();
        }
    }

    // ─────────────────────────────────────────────────────────
    // Carga AdminAreas disponibles — solo cuando rol = Empleado
    // ─────────────────────────────────────────────────────────
    function cargarAdmins() {
        const rolVal = document.getElementById('selRol').value;
        if (rolVal !== '3') return; // solo para Empleado

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
                    msgAdmin.textContent = '⚠ No hay AdminArea para esta área y turno. Crea uno primero.';
                    msgAdmin.className = 'form-text text-warning';
                } else {
                    msgAdmin.textContent = '';
                }
            });
    }

    // ─────────────────────────────────────────────────────────
    // DPI — formatea automáticamente mientras el usuario escribe
    // Formato Guatemala: 1234 56789 1234
    // ─────────────────────────────────────────────────────────
    document.getElementById('dpi').addEventListener('input', function() {
        let val = this.value.replace(/\D/g, ''); // solo dígitos
        if (val.length > 13) val = val.substring(0, 13);
        // Insertar espacios en posición 4 y 9
        let fmt = '';
        if (val.length <= 4)       fmt = val;
        else if (val.length <= 9)  fmt = val.substring(0,4) + ' ' + val.substring(4);
        else                       fmt = val.substring(0,4) + ' ' + val.substring(4,9) + ' ' + val.substring(9);
        this.value = fmt;
        // Validar longitud completa
        if (val.length === 13) {
            this.classList.remove('is-invalid');
            this.classList.add('is-valid');
        } else {
            this.classList.remove('is-valid');
            if (val.length > 0) this.classList.add('is-invalid');
        }
    });

    // ─────────────────────────────────────────────────────────
    // Validación final del form
    // ─────────────────────────────────────────────────────────
    document.getElementById('formAgregar').addEventListener('submit', function(e) {
        const rolVal   = document.getElementById('selRol').value;
        const selAdmin = document.getElementById('selAdmin');
        const hidden   = document.getElementById('idAdminAreaHidden');
        const dpi      = document.getElementById('dpi').value.replace(/\D/g,'');

        // Validar DPI Guatemala — exactamente 13 dígitos
        if (dpi.length !== 13) {
            e.preventDefault();
            alert('El DPI debe tener exactamente 13 dígitos. Formato: 1234 56789 1234');
            document.getElementById('dpi').focus();
            return;
        }

        if (rolVal === '3') {
            hidden.value = selAdmin.value;
            if (!selAdmin.value) {
                e.preventDefault();
                alert('Debes seleccionar un AdminArea para el empleado.');
            }
        } else {
            hidden.value = '0';
        }
    });
</script>
</body>
</html>
