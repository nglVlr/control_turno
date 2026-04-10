-- phpMyAdmin SQL Dump
-- version 4.9.2
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3308
-- Tiempo de generación: 10-04-2026 a las 21:53:24
-- Versión del servidor: 8.0.18
-- Versión de PHP: 7.3.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `control_turnos`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `areas`
--

DROP TABLE IF EXISTS `areas`;
CREATE TABLE IF NOT EXISTS `areas` (
  `id_area` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_area` varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` varchar(200) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_area`),
  UNIQUE KEY `nombre_area` (`nombre_area`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `areas`
--

INSERT INTO `areas` (`id_area`, `nombre_area`, `descripcion`, `activo`, `fecha_creacion`) VALUES
(1, 'Recursos Humanos', 'Dpto RRHH', 1, '2026-04-10 14:55:50');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignacion_turnos`
--

DROP TABLE IF EXISTS `asignacion_turnos`;
CREATE TABLE IF NOT EXISTS `asignacion_turnos` (
  `id_asignacion` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `id_turno` int(11) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `estado` enum('Vigente','Modificada','Cancelada') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Vigente',
  `id_admin_asigno` int(11) NOT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_asignacion`),
  KEY `idx_ast_empleado_fecha` (`id_empleado`,`fecha_inicio`,`fecha_fin`),
  KEY `idx_ast_estado` (`estado`),
  KEY `fk_ast_turno` (`id_turno`),
  KEY `fk_ast_admin` (`id_admin_asigno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bitacora`
--

DROP TABLE IF EXISTS `bitacora`;
CREATE TABLE IF NOT EXISTS `bitacora` (
  `id_log` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) DEFAULT NULL,
  `login` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `modulo` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `tipo_operacion` enum('Login','Crear','Aprobar','Rechazar','Inactivar','Marcaje','Solicitar') COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_spanish_ci,
  `fecha_hora` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_log`),
  KEY `idx_bitacora_fecha` (`fecha_hora`),
  KEY `idx_bitacora_emp` (`id_empleado`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `bitacora`
--

INSERT INTO `bitacora` (`id_log`, `id_empleado`, `login`, `modulo`, `tipo_operacion`, `descripcion`, `fecha_hora`) VALUES
(1, 2, 'JORG9', 'Marcaje', 'Marcaje', 'Entrada marcada por JORGITO RODRIGUEZ', '2026-04-10 15:44:03'),
(2, 2, 'JORG9', 'Marcaje', 'Marcaje', 'Descanso 1 marcado por JORGITO RODRIGUEZ', '2026-04-10 15:44:13'),
(3, 2, 'JORG9', 'Marcaje', 'Marcaje', 'Descanso 2 marcado por JORGITO RODRIGUEZ', '2026-04-10 15:44:14'),
(4, 2, 'JORG9', 'Marcaje', 'Marcaje', 'Salida marcada por JORGITO RODRIGUEZ', '2026-04-10 15:44:15');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleados`
--

DROP TABLE IF EXISTS `empleados`;
CREATE TABLE IF NOT EXISTS `empleados` (
  `id_empleado` int(11) NOT NULL AUTO_INCREMENT,
  `dpi` varchar(20) COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_spanish_ci NOT NULL,
  `usuario` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `contrasena` varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `correo` varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
  `id_area` int(11) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `id_turno_default` int(11) DEFAULT NULL,
  `estado` enum('Activo','Inactivo') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Activo',
  `motivo_inactivacion` enum('Permiso Personal','Vacaciones','Cita al IGSS','Licencia de Cumpleanos','Suspension Laboral','Otros') COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `dias_vacaciones` int(11) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_empleado`),
  UNIQUE KEY `dpi` (`dpi`),
  UNIQUE KEY `usuario` (`usuario`),
  UNIQUE KEY `correo` (`correo`),
  KEY `fk_emp_area` (`id_area`),
  KEY `fk_emp_rol` (`id_rol`),
  KEY `fk_emp_turno` (`id_turno_default`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `empleados`
--

INSERT INTO `empleados` (`id_empleado`, `dpi`, `nombre_completo`, `usuario`, `contrasena`, `correo`, `id_area`, `id_rol`, `id_turno_default`, `estado`, `motivo_inactivacion`, `dias_vacaciones`, `fecha_creacion`, `fecha_modificacion`) VALUES
(1, '1234567890101', 'Administrador RRHH', 'admin', 'admin123', 'admin@empresa.com', 1, 1, 1, 'Activo', NULL, 0, '2026-04-10 14:55:50', NULL),
(2, '11111111111111', 'JORGITO RODRIGUEZ', 'JORG9', 'jor123', 'jorgdriguez9@gmail.com', 1, 3, 4, 'Activo', NULL, 0, '2026-04-10 15:14:44', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `marcajes`
--

DROP TABLE IF EXISTS `marcajes`;
CREATE TABLE IF NOT EXISTS `marcajes` (
  `id_marcaje` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `fecha_marcaje` date NOT NULL,
  `hora_entrada` time DEFAULT NULL,
  `hora_descanso1` time DEFAULT NULL,
  `hora_descanso2` time DEFAULT NULL,
  `hora_salida` time DEFAULT NULL,
  `entrada_tarde` tinyint(1) NOT NULL DEFAULT '0',
  `observaciones` varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id_marcaje`),
  UNIQUE KEY `uq_marc_emp_fecha` (`id_empleado`,`fecha_marcaje`),
  KEY `idx_marcajes_fecha` (`fecha_marcaje`),
  KEY `idx_marcajes_empleado` (`id_empleado`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `marcajes`
--

INSERT INTO `marcajes` (`id_marcaje`, `id_empleado`, `fecha_marcaje`, `hora_entrada`, `hora_descanso1`, `hora_descanso2`, `hora_salida`, `entrada_tarde`, `observaciones`) VALUES
(1, 2, '2026-04-10', '15:44:03', '15:44:13', '15:44:14', '15:44:15', 1, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles` (
  `id_rol` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_rol` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion` varchar(200) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre_rol` (`nombre_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id_rol`, `nombre_rol`, `descripcion`, `activo`, `fecha_creacion`) VALUES
(1, 'AdminRRHH', 'Administrador de Recursos Humanos', 1, '2026-03-27 19:59:45'),
(2, 'AdminArea', 'Administrador de Area', 1, '2026-03-27 19:59:45'),
(3, 'Empleado', 'Empleado general', 1, '2026-03-27 19:59:45');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `solicitudes_cambio_turno`
--

DROP TABLE IF EXISTS `solicitudes_cambio_turno`;
CREATE TABLE IF NOT EXISTS `solicitudes_cambio_turno` (
  `id_solicitud_ct` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `fecha_inicial` date NOT NULL,
  `id_turno_inicial` int(11) NOT NULL,
  `fecha_nueva` date NOT NULL,
  `id_turno_nuevo` int(11) NOT NULL,
  `justificacion` text COLLATE utf8mb4_spanish_ci,
  `estado` enum('Pendiente','Aprobado','Rechazado') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `id_admin_resolvio` int(11) DEFAULT NULL,
  `fecha_resolucion` datetime DEFAULT NULL,
  `observacion_admin` varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_solicitud_ct`),
  KEY `idx_sct_estado` (`estado`),
  KEY `fk_sct_emp` (`id_empleado`),
  KEY `fk_sct_turno_ini` (`id_turno_inicial`),
  KEY `fk_sct_turno_nvo` (`id_turno_nuevo`),
  KEY `fk_sct_admin` (`id_admin_resolvio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `solicitudes_gestion`
--

DROP TABLE IF EXISTS `solicitudes_gestion`;
CREATE TABLE IF NOT EXISTS `solicitudes_gestion` (
  `id_solicitud` int(11) NOT NULL AUTO_INCREMENT,
  `id_empleado` int(11) NOT NULL,
  `id_tipo_gestion` int(11) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `motivo` text COLLATE utf8mb4_spanish_ci,
  `estado` enum('Pendiente','Aprobada AdminArea','Pendiente RRHH','Aprobada RRHH','Rechazada AdminArea','Rechazada RRHH') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `id_admin_resolvio` int(11) DEFAULT NULL,
  `fecha_resolucion` datetime DEFAULT NULL,
  `observacion_admin` varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_solicitud`),
  KEY `idx_sg_estado` (`estado`),
  KEY `fk_sg_emp` (`id_empleado`),
  KEY `fk_sg_tipo` (`id_tipo_gestion`),
  KEY `fk_sg_admin` (`id_admin_resolvio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipos_gestion`
--

DROP TABLE IF EXISTS `tipos_gestion`;
CREATE TABLE IF NOT EXISTS `tipos_gestion` (
  `id_tipo_gestion` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `categoria` enum('Licencia','Vacaciones','Permiso','Suspension','Otro') COLLATE utf8mb4_spanish_ci NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_tipo_gestion`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `tipos_gestion`
--

INSERT INTO `tipos_gestion` (`id_tipo_gestion`, `nombre`, `categoria`, `activo`) VALUES
(1, 'Vacaciones', 'Vacaciones', 1),
(2, 'Permiso Personal', 'Permiso', 1),
(3, 'Cita al IGSS', 'Permiso', 1),
(4, 'Licencia de Cumpleanos', 'Licencia', 1),
(5, 'Suspension Laboral', 'Suspension', 1),
(6, 'Otros', 'Otro', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `turnos`
--

DROP TABLE IF EXISTS `turnos`;
CREATE TABLE IF NOT EXISTS `turnos` (
  `id_turno` int(11) NOT NULL AUTO_INCREMENT,
  `nombre_turno` varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `horas_duracion` tinyint(4) NOT NULL DEFAULT '8',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_turno`),
  UNIQUE KEY `nombre_turno` (`nombre_turno`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `turnos`
--

INSERT INTO `turnos` (`id_turno`, `nombre_turno`, `hora_inicio`, `hora_fin`, `horas_duracion`, `activo`) VALUES
(1, 'Matutino', '06:00:00', '14:00:00', 8, 1),
(2, 'Vespertino', '14:00:00', '22:00:00', 8, 1),
(3, 'Diurno', '08:00:00', '16:00:00', 8, 1),
(4, 'Nocturno', '22:00:00', '06:00:00', 8, 1);

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `asignacion_turnos`
--
ALTER TABLE `asignacion_turnos`
  ADD CONSTRAINT `fk_ast_admin` FOREIGN KEY (`id_admin_asigno`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_ast_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_ast_turno` FOREIGN KEY (`id_turno`) REFERENCES `turnos` (`id_turno`);

--
-- Filtros para la tabla `bitacora`
--
ALTER TABLE `bitacora`
  ADD CONSTRAINT `fk_bit_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`);

--
-- Filtros para la tabla `empleados`
--
ALTER TABLE `empleados`
  ADD CONSTRAINT `fk_emp_area` FOREIGN KEY (`id_area`) REFERENCES `areas` (`id_area`),
  ADD CONSTRAINT `fk_emp_rol` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`),
  ADD CONSTRAINT `fk_emp_turno` FOREIGN KEY (`id_turno_default`) REFERENCES `turnos` (`id_turno`);

--
-- Filtros para la tabla `marcajes`
--
ALTER TABLE `marcajes`
  ADD CONSTRAINT `fk_marc_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`);

--
-- Filtros para la tabla `solicitudes_cambio_turno`
--
ALTER TABLE `solicitudes_cambio_turno`
  ADD CONSTRAINT `fk_sct_admin` FOREIGN KEY (`id_admin_resolvio`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_sct_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_sct_turno_ini` FOREIGN KEY (`id_turno_inicial`) REFERENCES `turnos` (`id_turno`),
  ADD CONSTRAINT `fk_sct_turno_nvo` FOREIGN KEY (`id_turno_nuevo`) REFERENCES `turnos` (`id_turno`);

--
-- Filtros para la tabla `solicitudes_gestion`
--
ALTER TABLE `solicitudes_gestion`
  ADD CONSTRAINT `fk_sg_admin` FOREIGN KEY (`id_admin_resolvio`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_sg_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados` (`id_empleado`),
  ADD CONSTRAINT `fk_sg_tipo` FOREIGN KEY (`id_tipo_gestion`) REFERENCES `tipos_gestion` (`id_tipo_gestion`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
