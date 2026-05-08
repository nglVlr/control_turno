<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="controlTurnos.modelo.Empleado"%>
<%@page import="java.util.List"%>
<%
    List<Empleado> admins = (List<Empleado>) request.getAttribute("listaAdmins");
%>
<option value="">Seleccionar AdminArea...</option>
<% if (admins != null) { for (Empleado a : admins) { %>
    <option value="<%= a.getIdEmpleado() %>">
        <%= a.getNombreCompleto() %>
    </option>
<% } } %>
