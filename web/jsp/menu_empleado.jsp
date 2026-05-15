<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.util.AppConfig"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%
    Empleado emp = (Empleado) session.getAttribute("empleado");
    if (emp == null || !emp.getNombreRol().equals("Empleado")) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
    String hora = new java.text.SimpleDateFormat("HH").format(new java.util.Date());
    int h = Integer.parseInt(hora);
    String saludo = h < 12 ? "Buenos días" : h < 19 ? "Buenas tardes" : "Buenas noches";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Panel Empleado — Control de Turnos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        body { background: #f0f4f8; }
        .menu-hero {
            background: linear-gradient(135deg, #6c3483 0%, #9b59b6 100%);
            border-radius: 16px; padding: 32px 36px; color: #fff;
            margin-bottom: 28px; position: relative; overflow: hidden;
        }
        .menu-hero::after {
            content: '👤';
            position: absolute; right: 36px; top: 50%;
            transform: translateY(-50%);
            font-size: 5rem; opacity: .1;
        }
        .menu-hero h4 { font-weight: 700; font-size: 1.5rem; margin: 0 0 4px; }
        .menu-hero p  { opacity: .8; margin: 0; font-size: .95rem; }
        .menu-hero .badges { margin-top: 12px; display: flex; gap: 8px; flex-wrap: wrap; }
        .menu-hero .badges span {
            background: rgba(255,255,255,.2);
            border-radius: 20px; padding: 3px 12px;
            font-size: .8rem; font-weight: 600;
        }
        .mcard {
            border: none; border-radius: 14px; padding: 28px 24px; background: #fff;
            box-shadow: 0 2px 12px rgba(44,62,80,.07);
            text-decoration: none; color: inherit;
            display: flex; flex-direction: column; align-items: center;
            text-align: center; gap: 12px;
            transition: transform .15s, box-shadow .15s; height: 100%;
        }
        .mcard:hover { transform: translateY(-3px); box-shadow: 0 8px 24px rgba(44,62,80,.13); color: inherit; }
        .mcard-icon {
            width: 64px; height: 64px; border-radius: 18px;
            display: flex; align-items: center; justify-content: center;
            font-size: 2rem;
        }
        .mcard-icon.blue   { background: #dbeafe; }
        .mcard-icon.green  { background: #dcfce7; }
        .mcard-icon.purple { background: #ede9fe; }
        .mcard h6 { font-weight: 700; margin: 0; font-size: 1rem; color: #1e293b; }
        .mcard p  { margin: 0; font-size: .82rem; color: #64748b; line-height: 1.4; }
    </style>
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos
        <span class="badge bg-secondary ms-1" style="font-size:.65rem;vertical-align:middle;"><%=AppConfig.VERSION%></span>
    </span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= emp.getNombreCompleto() %></span>
            &nbsp;|&nbsp; <%= emp.getNombreArea() %>
            &nbsp;|&nbsp; <%= emp.getNombreTurno() %>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">

    <div class="menu-hero">
        <h4><%= saludo %>, <%= emp.getNombreCompleto().split(" ")[0] %> 👋</h4>
        <p>Panel de Empleado</p>
        <div class="badges">
            <span>📍 <%= emp.getNombreArea() %></span>
            <span>🕐 <%= emp.getNombreTurno() %></span>
        </div>
    </div>

    <div class="row g-3 justify-content-center">

        <div class="col-lg-4 col-md-6">
            <a href="${pageContext.request.contextPath}/MarcajeServlet" class="mcard">
                <div class="mcard-icon blue">🕐</div>
                <div>
                    <h6>Marcaje</h6>
                    <p>Registrar tu entrada, descansos y salida del día</p>
                </div>
            </a>
        </div>

        <div class="col-lg-4 col-md-6">
            <a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=formularioGestion" class="mcard">
                <div class="mcard-icon green">📝</div>
                <div>
                    <h6>Gestiones</h6>
                    <p>Solicitar vacaciones, permisos, IGSS y más</p>
                </div>
            </a>
        </div>

        <div class="col-lg-4 col-md-6">
            <a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=formularioCambioTurno" class="mcard">
                <div class="mcard-icon purple">🔄</div>
                <div>
                    <h6>Cambio de Turno</h6>
                    <p>Solicitar un cambio de turno o área</p>
                </div>
            </a>
        </div>

    </div>
</div>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>
</body>
</html>
