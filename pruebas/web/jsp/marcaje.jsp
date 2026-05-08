<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="controlTurnos.modelo.Marcaje"%>
<%@page import="java.util.List"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null) { response.sendRedirect(request.getContextPath() + "/LoginServlet"); return; }
    Marcaje m = (Marcaje) request.getAttribute("marcajeHoy");
    List<Marcaje> listaMarcajes = (List<Marcaje>) request.getAttribute("listaMarcajes");

    boolean puedeEntrada   = (m == null);
    boolean puedeDescanso1 = (m != null && m.getHoraEntrada()   != null && m.getHoraDescanso1() == null);
    boolean puedeDescanso2 = (m != null && m.getHoraDescanso1() != null && m.getHoraDescanso2() == null);
    boolean puedeSalida    = (m != null && m.getHoraDescanso2() != null && m.getHoraSalida()    == null);
    boolean jornadaFin     = (m != null && m.getHoraSalida()    != null);

    String menuRegresar = "AdminArea".equals(sesion.getNombreRol())
        ? request.getContextPath() + "/jsp/menu_admin_area.jsp"
        : request.getContextPath() + "/jsp/menu_empleado.jsp";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Marcaje</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        #reloj { font-size:2.8rem; font-weight:700; color:#2C3E50; letter-spacing:2px; }
        .btn-marcaje { min-width:155px; padding:12px 16px; font-size:0.95rem; font-weight:600; border-radius:10px; }
        .step-item { display:flex; align-items:center; gap:12px; padding:10px 0; border-bottom:1px solid #f0f0f0; }
        .step-item:last-child { border-bottom:none; }
        .step-circle { width:36px; height:36px; border-radius:50%; display:flex; align-items:center; justify-content:center; font-weight:700; font-size:14px; flex-shrink:0; }
        .step-done    { background-color:#d4edda; color:#155724; }
        .step-pending { background-color:#f8f9fa; color:#adb5bd; border:2px dashed #dee2e6; }
        .step-active  { background-color:#cce5ff; color:#004085; border:2px solid #3498DB; }
        .hora-display { font-size:1.05rem; font-weight:600; color:#2C3E50; }
        .hora-vacia   { color:#adb5bd; font-style:italic; font-size:0.85rem; }
    </style>
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= sesion.getNombreRol() %>
            &nbsp;|&nbsp; <%= sesion.getNombreArea() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="<%= menuRegresar %>">Inicio</a></li>
            <li class="breadcrumb-item active">Marcaje</li>
        </ol>
    </nav>
    <h5 class="page-title">Marcaje del Día</h5>

    <% if (request.getAttribute("exito") != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= request.getAttribute("exito") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (request.getAttribute("advertencia") != null) { %>
        <div class="alert alert-warning alert-dismissible fade show" role="alert">
            <strong>⚠ Atención:</strong> <%= request.getAttribute("advertencia") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <%= request.getAttribute("error") %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    <% } %>

    <div class="row g-4">
        <!-- Panel izquierdo: Timer y botones -->
        <div class="col-lg-7">
            <div class="card shadow-sm border-0 p-4">
                <div class="text-center mb-4">
                    <div id="reloj">00:00:00</div>
                    <div class="text-muted small mt-1" id="fechaHoy"></div>
                </div>
                <% if (jornadaFin) { %>
                    <div class="alert alert-success text-center fw-semibold mb-0">✓ Jornada completada. ¡Hasta mañana!</div>
                <% } else { %>
                    <div class="d-flex flex-wrap justify-content-center gap-3">
                        <form action="${pageContext.request.contextPath}/MarcajeServlet" method="post">
                            <input type="hidden" name="accion" value="entrada">
                            <button type="submit" class="btn btn-success btn-marcaje" <%= !puedeEntrada ? "disabled" : "" %>>↓ Marcar Entrada</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/MarcajeServlet" method="post">
                            <input type="hidden" name="accion" value="descanso1">
                            <button type="submit" class="btn btn-warning btn-marcaje" <%= !puedeDescanso1 ? "disabled" : "" %>>☕ Descanso 1</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/MarcajeServlet" method="post">
                            <input type="hidden" name="accion" value="descanso2">
                            <button type="submit" class="btn btn-warning btn-marcaje" <%= !puedeDescanso2 ? "disabled" : "" %>>☕ Descanso 2</button>
                        </form>
                        <form action="${pageContext.request.contextPath}/MarcajeServlet" method="post">
                            <input type="hidden" name="accion" value="salida">
                            <button type="submit" class="btn btn-danger btn-marcaje" <%= !puedeSalida ? "disabled" : "" %>>↑ Marcar Salida</button>
                        </form>
                    </div>
                <% } %>
            </div>
        </div>

        <!-- Panel derecho: Información del Marcaje -->
        <div class="col-lg-5">
            <div class="card shadow-sm border-0 p-4">
                <h6 class="fw-bold mb-3" style="color:#2C3E50;">Información del Marcaje</h6>
                <div class="step-item">
                    <div class="step-circle <%= (m != null && m.getHoraEntrada() != null) ? "step-done" : puedeEntrada ? "step-active" : "step-pending" %>">1</div>
                    <div>
                        <div class="small fw-semibold">Entrada
                            <% if (m != null && m.getEntradaTarde() == 1) { %><span class="badge badge-pendiente ms-1 px-2 py-1 rounded">Tarde</span><% } %>
                        </div>
                        <% if (m != null && m.getHoraEntrada() != null) { %><div class="hora-display"><%= m.getHoraEntrada() %></div>
                        <% } else { %><div class="hora-vacia">Sin registrar</div><% } %>
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-circle <%= (m != null && m.getHoraDescanso1() != null) ? "step-done" : puedeDescanso1 ? "step-active" : "step-pending" %>">2</div>
                    <div>
                        <div class="small fw-semibold">Descanso 1</div>
                        <% if (m != null && m.getHoraDescanso1() != null) { %><div class="hora-display"><%= m.getHoraDescanso1() %></div>
                        <% } else { %><div class="hora-vacia">Sin registrar</div><% } %>
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-circle <%= (m != null && m.getHoraDescanso2() != null) ? "step-done" : puedeDescanso2 ? "step-active" : "step-pending" %>">3</div>
                    <div>
                        <div class="small fw-semibold">Descanso 2</div>
                        <% if (m != null && m.getHoraDescanso2() != null) { %><div class="hora-display"><%= m.getHoraDescanso2() %></div>
                        <% } else { %><div class="hora-vacia">Sin registrar</div><% } %>
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-circle <%= (m != null && m.getHoraSalida() != null) ? "step-done" : puedeSalida ? "step-active" : "step-pending" %>">4</div>
                    <div>
                        <div class="small fw-semibold">Salida</div>
                        <% if (m != null && m.getHoraSalida() != null) { %><div class="hora-display"><%= m.getHoraSalida() %></div>
                        <% } else { %><div class="hora-vacia">Sin registrar</div><% } %>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <%-- Si es AdminArea, muestra tabla de marcajes de su área y turno --%>
    <% if ("AdminArea".equals(sesion.getNombreRol()) && listaMarcajes != null && !listaMarcajes.isEmpty()) { %>
        <div class="mt-4">
            <h6 class="fw-bold mb-3" style="color:#2C3E50;">Marcajes de tu área hoy — <%= sesion.getNombreArea() %> / <%= sesion.getNombreTurno() %></h6>
            <div class="card shadow-sm border-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0" id="tablaMarcajes">
                        <thead class="tabla-header">
                            <tr><th>Empleado</th><th>Entrada</th><th>Descanso 1</th><th>Descanso 2</th><th>Salida</th><th>Estado</th></tr>
                        </thead>
                        <tbody>
                            <% for (Marcaje mr : listaMarcajes) { %>
                                <tr>
                                    <td><%= mr.getNombreEmpleado() %></td>
                                    <td><%= mr.getHoraEntrada()   != null ? mr.getHoraEntrada()   : "—" %></td>
                                    <td><%= mr.getHoraDescanso1() != null ? mr.getHoraDescanso1() : "—" %></td>
                                    <td><%= mr.getHoraDescanso2() != null ? mr.getHoraDescanso2() : "—" %></td>
                                    <td><%= mr.getHoraSalida()    != null ? mr.getHoraSalida()    : "—" %></td>
                                    <td>
                                        <% if (mr.getHoraSalida() != null) { %>
                                            <span class="badge badge-aprobado px-2 py-1 rounded">Completo</span>
                                        <% } else if (mr.getHoraEntrada() != null) { %>
                                            <span class="badge badge-pendiente px-2 py-1 rounded">En turno</span>
                                            <% if (mr.getEntradaTarde() == 1) { %>
                                                <span class="badge badge-rechazado px-2 py-1 rounded ms-1">Tarde</span>
                                            <% } %>
                                        <% } else { %>
                                            <span class="badge badge-inactivo px-2 py-1 rounded">Sin marcar</span>
                                        <% } %>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    <% } %>

    <div class="mt-3">
        <a href="<%= menuRegresar %>" class="btn btn-secondary btn-sm">Regresar</a>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
<script>
    // Reloj en tiempo real
    function actualizarReloj() {
        const ahora = new Date();
        const h = String(ahora.getHours()).padStart(2,'0');
        const mi = String(ahora.getMinutes()).padStart(2,'0');
        const s = String(ahora.getSeconds()).padStart(2,'0');
        document.getElementById('reloj').textContent = h + ':' + mi + ':' + s;
        const dias   = ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'];
        const meses  = ['enero','febrero','marzo','abril','mayo','junio','julio','agosto','septiembre','octubre','noviembre','diciembre'];
        document.getElementById('fechaHoy').textContent =
            dias[ahora.getDay()] + ', ' + ahora.getDate() + ' de ' + meses[ahora.getMonth()] + ' de ' + ahora.getFullYear();
    }
    actualizarReloj();
    setInterval(actualizarReloj, 1000);

    // Polling cada 15 segundos para actualizar tabla de marcajes del área (tiempo real)
    <% if ("AdminArea".equals(sesion.getNombreRol())) { %>
    setInterval(function() {
        fetch('${pageContext.request.contextPath}/MarcajeServlet?accion=listar')
            .then(function() { location.reload(); });
    }, 15000);
    <% } %>
</script>
</body>
</html>
