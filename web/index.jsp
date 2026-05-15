<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Redirige automáticamente al LoginServlet al entrar al sistema
    response.sendRedirect(request.getContextPath() + "/LoginServlet");
%>
