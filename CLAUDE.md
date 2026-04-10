# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

This is a **NetBeans Web Project** built with Ant and deployed on Apache Tomcat.

- **Build:** Open the project in NetBeans IDE and use _Run > Build Project_ (F11), or from terminal:
  ```
  ant -f build.xml
  ```
- **Deploy & Run:** In NetBeans use _Run > Run Project_ (F6). NetBeans handles WAR deployment to the configured Tomcat instance automatically.
- **Clean:** `ant -f build.xml clean`

There are no automated tests in this project.

## Database

- **Engine:** MySQL
- **Port:** 3308 (non-standard — not 3306)
- **Database name:** `control_turnos`
- **Credentials:** root / (empty password) — configured in `src/controlTurnos/util/Conexion.java`
- **Driver:** `mysql-connector-java-5.1.49.jar` (bundled in `mysql-connector-java-5.1.49/` and copied to `WEB-INF/lib` at build time)
- Passwords are stored in plain text in the `empleados` table.

## Architecture

The application follows a classic **MVC pattern** without a framework:

```
src/java/controlTurnos/        ← source root (src.dir=src/java en nbproject/project.properties)
├── modelo/      — POJOs: Empleado, Rol, Area, Turno
├── dao/         — JDBC data access: EmpleadoDAO, RolDAO, AreaDAO, TurnoDAO
├── servlet/     — HttpServlet controllers: LoginServlet, LogoutServlet, EmpleadoServlet
└── util/        — Conexion.java (static getConexion / cerrarConexion)

web/
├── login.jsp
├── css/         — Bootstrap 5 + custom style.css
├── js/          — Bootstrap bundle
├── jsp/         — Protected JSP views (menu_admin_rrhh, menu_admin_area, menu_empleado,
│                  agregar_empleado, consultar_empleado)
└── WEB-INF/web.xml
```

**Request flow:** Browser → Servlet (doGet/doPost) → DAO → DB → Servlet sets request attributes → forwards to JSP.

**Session & authorization:**
- On login, `LoginServlet` stores the full `Empleado` object in the HTTP session under the key `"empleado"`.
- Role-based routing uses `empleado.getNombreRol()` which returns one of three string values: `"AdminRRHH"`, `"AdminArea"`, `"Empleado"`.
- Every protected servlet and every JSP checks the session and role at the top before processing. Servlets call a private `validarSesion()` helper; JSPs do the check inline with a scriptlet at the top of the file.

**DAO pattern:** Each DAO opens a connection via `Conexion.getConexion()`, executes a `PreparedStatement`, and closes all resources in a `finally` block. There is no connection pool — each call creates a new JDBC connection. The `Empleado` model carries denormalized display fields (`nombreRol`, `nombreArea`, `nombreTurno`) populated by JOIN queries to avoid extra lookups in the view layer.

## Key conventions

- `Area`, `Turno`, `Rol` use an `int activo` field (1 = active, 0 = inactive). `Empleado` uses a `String estado` field (`"Activo"` / `"Inactivo"`).
- Servlet URL mappings are defined both via `@WebServlet` annotation and in `web.xml` (both must stay in sync).
- JSP views live under `web/jsp/` and reference CSS/JS with relative paths (`../css/`, `../js/`). `login.jsp` lives directly under `web/` and uses paths without the `../` prefix.
- Comments in DAOs reference use-case steps (e.g. `// CU1 paso 18`) tied to a requirements specification document.
