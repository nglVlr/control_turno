<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.util.AppConfig"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema Control de Turnos</title>
    <link rel="stylesheet" href="css/bootstrap.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="login-wrapper">
    <div class="login-card p-4">
        <div class="card shadow-sm border-0">
            <div class="card-body p-4">
                <div class="text-center mb-4">
                    <div class="login-logo mb-3">
                        <svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" fill="white" viewBox="0 0 16 16">
                            <path d="M8 3.5a.5.5 0 0 0-1 0V9a.5.5 0 0 0 .252.434l3.5 2a.5.5 0 0 0 .496-.868L8 8.71V3.5z"/>
                            <path d="M8 16A8 8 0 1 0 8 0a8 8 0 0 0 0 16zm7-8A7 7 0 1 1 1 8a7 7 0 0 1 14 0z"/>
                        </svg>
                    </div>
                    <h4 class="fw-bold mb-1" style="color:#2C3E50;">Control de Turnos</h4>
                    <p class="text-muted small">Ingresa tus credenciales para continuar</p>
                    <span class="badge bg-secondary" style="font-size:0.7rem;"><%=AppConfig.VERSION%></span>
                </div>
                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show py-2" role="alert">
                        <small><%= request.getAttribute("error") %></small>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                <% } %>
                <form action="LoginServlet" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Usuario</label>
                        <input type="text" class="form-control" name="usuario" placeholder="Ingresa tu usuario" required autofocus>
                    </div>
                    <div class="mb-4">
                        <label class="form-label fw-semibold">Contraseña</label>
                        <input type="password" class="form-control" name="contrasena" placeholder="Ingresa tu contraseña" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100 fw-semibold">Iniciar Sesión</button>
                </form>
            </div>
        </div>
    </div>
</div>
<script src="js/bootstrap.bundle.min.js"></script>
</body>
</html>
