<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%
    Empleado sesion = (Empleado) session.getAttribute("empleado");
    if (sesion == null || !"Empleado".equals(sesion.getNombreRol())) {
        response.sendRedirect(request.getContextPath() + "/LoginServlet"); return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nueva Gestión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar navbar-custom navbar-expand-lg px-4 py-2">
    <span class="navbar-brand text-white fw-bold">Control de Turnos</span>
    <div class="ms-auto d-flex align-items-center gap-3">
        <span class="text-white-50 small">
            <span class="text-white fw-semibold"><%= sesion.getNombreCompleto() %></span>
        </span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn btn-outline-light btn-sm">Cerrar Sesión</a>
    </div>
</nav>

<div class="container mt-4">
    <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/jsp/menu_empleado.jsp">Inicio</a></li>
            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/EmpleadoGestionServlet?accion=menu">Mis Gestiones</a></li>
            <li class="breadcrumb-item active">Nueva Gestión</li>
        </ol>
    </nav>
    <h5 class="page-title">Nueva Gestión</h5>

    <div class="card shadow-sm border-0" style="max-width:600px;">
        <div class="card-body p-4">
            <form action="${pageContext.request.contextPath}/EmpleadoGestionServlet" method="post">
                <input type="hidden" name="accion" value="crearGestion">
                <div class="row g-3">

                    <%-- CU5 paso 5a: tipos de gestión --%>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Tipo de Gestión <span class="text-danger">*</span></label>
                        <select class="form-select" name="idTipoGestion" required>
                            <option value="">Seleccionar...</option>
                            <option value="1">Vacaciones</option>
                            <option value="2">Permiso Personal</option>
                            <option value="3">Cita al IGSS</option>
                            <option value="4">Licencia de Cumpleaños</option>
                            <option value="5">Suspensión Laboral</option>
                            <option value="6">Otros</option>
                        </select>
                    </div>

                    <%-- CU5 paso 5b: fecha inicio --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Inicio <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaInicio" required>
                    </div>

                    <%-- CU5 paso 5c: fecha fin --%>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Fecha Fin <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" name="fechaFin" required>
                    </div>

                    <%-- CU5 paso 5d: motivo --%>
                    <div class="col-md-12">
                        <label class="form-label fw-semibold">Motivo de Solicitud</label>
                        <textarea class="form-control" name="motivo" rows="3"
                                  placeholder="Describe el motivo de tu solicitud..."></textarea>
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
    // Validar que fecha fin no sea anterior a inicio
    document.querySelector('form').addEventListener('submit', function(e) {
        const fi = document.querySelector('[name=fechaInicio]').value;
        const ff = document.querySelector('[name=fechaFin]').value;
        if (fi && ff && ff < fi) {
            e.preventDefault();
            alert('La fecha fin no puede ser anterior a la fecha inicio.');
        }
    });
</script>
</body>
</html>
