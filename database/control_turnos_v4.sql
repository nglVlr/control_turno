-- phpMyAdmin SQL Dump
-- Sistema Control de Turnos — v4
-- Basado en BD actual del usuario + nuevos requerimientos:
--   1. Columnas id_admin_area, creado_por, modificado_por en empleados
--   2. Contraseñas encriptadas con MD5
--   3. Tabla bitacora_detalle
--   4. Vistas de reporte
-- Servidor: 127.0.0.1:3308 | MySQL 8.0.18

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";
SET FOREIGN_KEY_CHECKS = 0;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

DROP DATABASE IF EXISTS `control_turnos`;
CREATE DATABASE IF NOT EXISTS `control_turnos`
  CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE `control_turnos`;

-- ============================================================
-- 1. ROLES
-- ============================================================
CREATE TABLE `roles` (
  `id_rol`        int(11)      NOT NULL AUTO_INCREMENT,
  `nombre_rol`    varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion`   varchar(200) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `activo`        tinyint(1)   NOT NULL DEFAULT '1',
  `fecha_creacion` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre_rol` (`nombre_rol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `roles` VALUES
(1,'AdminRRHH', 'Administrador de Recursos Humanos',1,'2026-04-18 02:51:10'),
(2,'AdminArea',  'Administrador de Area',             1,'2026-04-18 02:51:10'),
(3,'Empleado',   'Empleado general',                  1,'2026-04-18 02:51:10');

-- ============================================================
-- 2. AREAS
-- ============================================================
CREATE TABLE `areas` (
  `id_area`       int(11)      NOT NULL AUTO_INCREMENT,
  `nombre_area`   varchar(100) COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion`   varchar(200) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `activo`        tinyint(1)   NOT NULL DEFAULT '1',
  `fecha_creacion` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_area`),
  UNIQUE KEY `nombre_area` (`nombre_area`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `areas` VALUES
(1,'Recursos Humanos','Dpto RRHH',          1,'2026-04-18 02:51:10'),
(2,'IT',              'Dpto IT',             1,'2026-04-18 02:51:10'),
(3,'Contabilidad',    'Dpto Conta',          1,'2026-04-18 02:51:10'),
(4,'Ventas',          'Dpto Ventas',         1,'2026-04-18 02:51:10'),
(5,'Mantenimiento',   'Dpto Mantenimiento',  1,'2026-04-18 02:51:10');

-- ============================================================
-- 3. TURNOS
-- ============================================================
CREATE TABLE `turnos` (
  `id_turno`       int(11)     NOT NULL AUTO_INCREMENT,
  `nombre_turno`   varchar(50) COLLATE utf8mb4_spanish_ci NOT NULL,
  `hora_inicio`    time        NOT NULL,
  `hora_fin`       time        NOT NULL,
  `horas_duracion` tinyint(4)  NOT NULL DEFAULT '8',
  `activo`         tinyint(1)  NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_turno`),
  UNIQUE KEY `nombre_turno` (`nombre_turno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `turnos` VALUES
(1,'Matutino',  '06:00:00','14:00:00',8,1),
(2,'Vespertino','14:00:00','22:00:00',8,1),
(3,'Diurno',    '08:00:00','16:00:00',8,1),
(4,'Nocturno',  '22:00:00','06:00:00',8,1);

-- ============================================================
-- 4. EMPLEADOS
-- NUEVO: id_admin_area, creado_por, modificado_por
-- Contraseñas encriptadas con MD5
-- ============================================================
CREATE TABLE `empleados` (
  `id_empleado`        int(11)      NOT NULL AUTO_INCREMENT,
  `dpi`                varchar(20)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `nombre_completo`    varchar(150) COLLATE utf8mb4_spanish_ci NOT NULL,
  `usuario`            varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `contrasena`         varchar(255) COLLATE utf8mb4_spanish_ci NOT NULL,
  `correo`             varchar(120) COLLATE utf8mb4_spanish_ci NOT NULL,
  `id_area`            int(11)      NOT NULL,
  `id_rol`             int(11)      NOT NULL,
  `id_turno_default`   int(11)      DEFAULT NULL,
  -- Nuevo: a qué AdminArea pertenece este empleado
  `id_admin_area`      int(11)      DEFAULT NULL,
  `estado`             enum('Activo','Inactivo') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Activo',
  `motivo_inactivacion` enum('Permiso Personal','Vacaciones','Cita al IGSS','Licencia de Cumpleanos','Suspension Laboral','Otros')
                       COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `dias_vacaciones`    int(11)      NOT NULL DEFAULT '0',
  `fecha_creacion`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_modificacion` datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  -- Nuevo: auditoría de quién creó/modificó
  `creado_por`         int(11)      DEFAULT NULL,
  `modificado_por`     int(11)      DEFAULT NULL,
  PRIMARY KEY (`id_empleado`),
  UNIQUE KEY `dpi`     (`dpi`),
  UNIQUE KEY `usuario` (`usuario`),
  UNIQUE KEY `correo`  (`correo`),
  KEY `fk_emp_area`        (`id_area`),
  KEY `fk_emp_rol`         (`id_rol`),
  KEY `fk_emp_turno`       (`id_turno_default`),
  KEY `fk_emp_admin_area`  (`id_admin_area`),
  KEY `fk_emp_creado_por`  (`creado_por`),
  KEY `fk_emp_modif_por`   (`modificado_por`),
  KEY `idx_emp_area_turno` (`id_area`,`id_turno_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- Datos actuales del usuario — contraseñas encriptadas con MD5
-- Las contraseñas originales eran: admin123 para admin, 123 para los demás
INSERT INTO `empleados`
  (`id_empleado`,`dpi`,`nombre_completo`,`usuario`,`contrasena`,`correo`,
   `id_area`,`id_rol`,`id_turno_default`,`id_admin_area`,`estado`,
   `motivo_inactivacion`,`dias_vacaciones`,`fecha_creacion`,`fecha_modificacion`,
   `creado_por`,`modificado_por`) VALUES
(1,'0000000000001','Administrador RRHH','admin',     MD5('admin123'),'admin@empresa.com',   1,1,1,NULL,'Activo',NULL,0,'2026-04-18 02:51:10',NULL,NULL,NULL),
(2,'1234 12345 1234','Angel Amaliel',   'AnAm',      MD5('123'),     'AnAm123@gmail.com',   2,2,3,NULL,'Activo',NULL,0,'2026-04-17 21:17:22','2026-04-18 07:48:26',NULL,NULL),
(3,'1234 98765 4567','Josue Garcia',    'Garc',      MD5('123'),     'Garc@gmail.com',      2,3,4,2,   'Activo',NULL,0,'2026-04-18 07:49:51','2026-04-18 15:02:41',1,1),
(4,'1234 1235 49878','Nilson Marro',    'marro',     MD5('123'),     'marro123@gmail.com',  2,3,4,2,   'Activo',NULL,0,'2026-04-18 14:39:18','2026-04-18 16:18:30',1,1),
(5,'1234 15623 1264','Neftali Lopez',   'nefta',     MD5('123'),     'nefta123@gmail.com',  2,3,4,2,   'Activo',NULL,0,'2026-04-18 15:04:36',NULL,1,NULL),
(6,'8949 78944 4894','Eli',             'eli',       MD5('123'),     'eli123@gmail.com',    2,3,3,2,   'Activo',NULL,0,'2026-04-18 16:54:29',NULL,1,NULL);

-- ============================================================
-- 5. MARCAJES (sin cambios en estructura)
-- ============================================================
CREATE TABLE `marcajes` (
  `id_marcaje`    int(11)      NOT NULL AUTO_INCREMENT,
  `id_empleado`   int(11)      NOT NULL,
  `fecha_marcaje` date         NOT NULL,
  `hora_entrada`  time         DEFAULT NULL,
  `hora_descanso1` time        DEFAULT NULL,
  `hora_descanso2` time        DEFAULT NULL,
  `hora_salida`   time         DEFAULT NULL,
  `entrada_tarde` tinyint(1)   NOT NULL DEFAULT '0',
  `observaciones` varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  PRIMARY KEY (`id_marcaje`),
  UNIQUE KEY `uq_marc_emp_fecha` (`id_empleado`,`fecha_marcaje`),
  KEY `idx_marcajes_fecha`    (`fecha_marcaje`),
  KEY `idx_marcajes_empleado` (`id_empleado`),
  KEY `idx_marc_emp_fecha`    (`id_empleado`,`fecha_marcaje`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `marcajes` VALUES
(1,3,'2026-04-18','07:50:21','07:50:26','07:50:28','07:50:29',0,NULL),
(2,4,'2026-04-18','14:58:00',NULL,NULL,NULL,1,NULL),
(3,5,'2026-04-18','15:04:52','15:07:28','15:07:29','15:07:30',1,NULL),
(4,2,'2026-04-18','15:08:06','16:29:50','16:29:53','16:29:54',1,NULL);

-- ============================================================
-- 6. ASIGNACION DE TURNOS
-- ============================================================
CREATE TABLE `asignacion_turnos` (
  `id_asignacion`  int(11)    NOT NULL AUTO_INCREMENT,
  `id_empleado`    int(11)    NOT NULL,
  `id_turno`       int(11)    NOT NULL,
  `fecha_inicio`   date       NOT NULL,
  `fecha_fin`      date       NOT NULL,
  `estado`         enum('Vigente','Modificada','Cancelada') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Vigente',
  `id_admin_asigno` int(11)   NOT NULL,
  `fecha_registro` datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_asignacion`),
  KEY `idx_ast_empleado_fecha` (`id_empleado`,`fecha_inicio`,`fecha_fin`),
  KEY `idx_ast_estado`  (`estado`),
  KEY `fk_ast_turno`   (`id_turno`),
  KEY `fk_ast_admin`   (`id_admin_asigno`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- ============================================================
-- 7. TIPOS DE GESTION
-- ============================================================
CREATE TABLE `tipos_gestion` (
  `id_tipo_gestion` int(11)    NOT NULL AUTO_INCREMENT,
  `nombre`          varchar(80) COLLATE utf8mb4_spanish_ci NOT NULL,
  `categoria`       enum('Licencia','Vacaciones','Permiso','Suspension','Otro') COLLATE utf8mb4_spanish_ci NOT NULL,
  `activo`          tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_tipo_gestion`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `tipos_gestion` VALUES
(1,'Vacaciones',            'Vacaciones', 1),
(2,'Permiso Personal',      'Permiso',    1),
(3,'Cita al IGSS',          'Permiso',    1),
(4,'Licencia de Cumpleanos','Licencia',   1),
(5,'Suspension Laboral',    'Suspension', 1),
(6,'Otros',                 'Otro',       1);

-- ============================================================
-- 8. SOLICITUDES DE GESTION
-- ============================================================
CREATE TABLE `solicitudes_gestion` (
  `id_solicitud`     int(11)  NOT NULL AUTO_INCREMENT,
  `id_empleado`      int(11)  NOT NULL,
  `id_tipo_gestion`  int(11)  NOT NULL,
  `fecha_inicio`     date     NOT NULL,
  `fecha_fin`        date     NOT NULL,
  `motivo`           text     COLLATE utf8mb4_spanish_ci,
  `estado`           enum('Pendiente','Aprobada AdminArea','Pendiente RRHH','Aprobada RRHH','Rechazada AdminArea','Rechazada RRHH')
                     COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `id_admin_resolvio` int(11) DEFAULT NULL,
  `fecha_resolucion`  datetime DEFAULT NULL,
  `observacion_admin` varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `fecha_creacion`    datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_solicitud`),
  KEY `idx_sg_estado` (`estado`),
  KEY `fk_sg_emp`     (`id_empleado`),
  KEY `fk_sg_tipo`    (`id_tipo_gestion`),
  KEY `fk_sg_admin`   (`id_admin_resolvio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `solicitudes_gestion` VALUES
(1,3,1,'2026-04-20','2026-04-24','Fallecimiento de un familiar','Pendiente',NULL,NULL,NULL,'2026-04-18 14:32:42'),
(2,4,2,'2026-04-20','2026-04-24','Se murio el perro','Aprobada RRHH',1,'2026-04-18 14:42:34','Pobre','2026-04-18 14:40:23'),
(3,4,3,'2026-04-20','2026-04-21','CITA IGGS','Aprobada RRHH',1,'2026-04-18 16:05:52','','2026-04-18 14:51:47');

-- ============================================================
-- 9. SOLICITUDES DE CAMBIO DE TURNO
-- ============================================================
CREATE TABLE `solicitudes_cambio_turno` (
  `id_solicitud_ct`    int(11)  NOT NULL AUTO_INCREMENT,
  `id_empleado`        int(11)  NOT NULL,
  `fecha_inicial`      date     NOT NULL,
  `id_turno_inicial`   int(11)  NOT NULL,
  `id_area_origen`     int(11)  NOT NULL,
  `fecha_nueva`        date     NOT NULL,
  `id_turno_nuevo`     int(11)  NOT NULL,
  `id_area_destino`    int(11)  NOT NULL,
  `justificacion`      text     COLLATE utf8mb4_spanish_ci,
  `estado`             enum('Pendiente','Aprobado','Rechazado') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `notif_admin_origen` enum('Pendiente','Visto')  COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `notif_admin_destino` enum('Pendiente','Visto') COLLATE utf8mb4_spanish_ci NOT NULL DEFAULT 'Pendiente',
  `id_rrhh_resolvio`   int(11)  DEFAULT NULL,
  `fecha_resolucion`   datetime DEFAULT NULL,
  `observacion_rrhh`   varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `fecha_creacion`     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_solicitud_ct`),
  KEY `idx_sct_estado`         (`estado`),
  KEY `fk_sct_emp`             (`id_empleado`),
  KEY `fk_sct_turno_ini`       (`id_turno_inicial`),
  KEY `fk_sct_turno_nvo`       (`id_turno_nuevo`),
  KEY `fk_sct_area_origen`     (`id_area_origen`),
  KEY `fk_sct_area_destino`    (`id_area_destino`),
  KEY `fk_sct_rrhh`            (`id_rrhh_resolvio`),
  KEY `idx_sct_notif_origen`   (`notif_admin_origen`),
  KEY `idx_sct_notif_destino`  (`notif_admin_destino`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `solicitudes_cambio_turno` VALUES
(1,3,'2026-04-18',3,5,'2026-05-09',4,2,'nuevo puesto laboral','Aprobado','Pendiente','Pendiente',1,'2026-04-18 15:02:41','','2026-04-18 15:02:06'),
(2,4,'2026-04-18',3,2,'2026-05-01',4,2,'Clases de la universidad','Aprobado','Visto','Pendiente',1,'2026-04-18 16:18:30','','2026-04-18 16:17:31');

-- ============================================================
-- 10. BITACORA
-- ============================================================
CREATE TABLE `bitacora` (
  `id_log`        bigint(20)   NOT NULL AUTO_INCREMENT,
  `id_empleado`   int(11)      DEFAULT NULL,
  `login`         varchar(50)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `modulo`        varchar(80)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `tipo_operacion` enum('Login','Crear','Aprobar','Rechazar','Inactivar','Marcaje','Solicitar','Asignar','Notificar')
                  COLLATE utf8mb4_spanish_ci NOT NULL,
  `descripcion`   text         COLLATE utf8mb4_spanish_ci,
  `fecha_hora`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_log`),
  KEY `idx_bitacora_fecha` (`fecha_hora`),
  KEY `idx_bitacora_emp`   (`id_empleado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

INSERT INTO `bitacora` (`id_log`,`id_empleado`,`login`,`modulo`,`tipo_operacion`,`descripcion`,`fecha_hora`) VALUES
(1,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 07:40:36'),
(2,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 07:48:09'),
(3,1,'admin','Roles','Crear','Rol cambiado a AdminArea para: Angel Amaliel | Area: IT | Turno: Diurno','2026-04-18 07:48:26'),
(4,1,'admin','Empleados','Crear','Empleado creado: Josue Garcia | Area: 5','2026-04-18 07:49:51'),
(5,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 07:50:10'),
(6,3,'Garc','Marcaje','Marcaje','Entrada marcada por Josue Garcia','2026-04-18 07:50:21'),
(7,3,'Garc','Marcaje','Marcaje','Descanso 1 marcado por Josue Garcia','2026-04-18 07:50:26'),
(8,3,'Garc','Marcaje','Marcaje','Descanso 2 marcado por Josue Garcia','2026-04-18 07:50:28'),
(9,3,'Garc','Marcaje','Marcaje','Salida marcada por Josue Garcia','2026-04-18 07:50:29'),
(10,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:31:35'),
(11,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 14:31:54'),
(12,3,'Garc','Gestiones','Solicitar','Gestión creada por Josue Garcia | Tipo id:1 | 2026-04-20 al 2026-04-24','2026-04-18 14:32:42'),
(13,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:33:12'),
(14,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:33:34'),
(15,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 14:34:20'),
(16,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:35:50'),
(17,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:38:13'),
(18,1,'admin','Empleados','Crear','Empleado creado: Nilson Marro | Area: 2','2026-04-18 14:39:18'),
(19,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 14:39:47'),
(20,4,'marro','Gestiones','Solicitar','Gestión creada por Nilson Marro | Tipo id:2 | 2026-04-20 al 2026-04-24','2026-04-18 14:40:23'),
(21,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 14:40:49'),
(22,2,'AnAm','Solicitudes','Aprobar','Gestion id:2 Aprobar por AdminArea | Obs: Pobrecito','2026-04-18 14:41:38'),
(23,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:42:14'),
(24,1,'admin','Solicitudes','Aprobar','Gestion id:2 Aprobar por RRHH | Obs: Pobre','2026-04-18 14:42:34'),
(25,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 14:48:09'),
(26,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 14:49:03'),
(27,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:50:06'),
(28,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 14:51:28'),
(29,4,'marro','Gestiones','Solicitar','Gestión creada por Nilson Marro | Tipo id:3 | 2026-04-20 al 2026-04-21','2026-04-18 14:51:47'),
(30,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:52:58'),
(31,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 14:53:07'),
(32,2,'AnAm','Solicitudes','Aprobar','Gestion id:3 Aprobar por AdminArea','2026-04-18 14:53:45'),
(33,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 14:53:54'),
(34,4,'marro','Marcaje','Marcaje','Entrada marcada por Nilson Marro','2026-04-18 14:58:00'),
(35,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 14:58:25'),
(36,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 15:01:18'),
(37,3,'Garc','Cambio Turno','Solicitar','Cambio de turno solicitado por Josue Garcia | Turno nuevo id:4 | Area destino id:2','2026-04-18 15:02:06'),
(38,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 15:02:14'),
(39,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 15:02:27'),
(40,1,'admin','Solicitudes','Aprobar','Cambio de turno id:1 Aprobado','2026-04-18 15:02:41'),
(41,3,'Garc','Login','Login','Inicio de sesion: Josue Garcia','2026-04-18 15:03:13'),
(42,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 15:03:50'),
(43,1,'admin','Empleados','Crear','Empleado creado: Neftali Lopez | Area: 2','2026-04-18 15:04:36'),
(44,5,'nefta','Login','Login','Inicio de sesion: Neftali Lopez','2026-04-18 15:04:50'),
(45,5,'nefta','Marcaje','Marcaje','Entrada marcada por Neftali Lopez','2026-04-18 15:04:52'),
(46,5,'nefta','Login','Login','Inicio de sesion: Neftali Lopez','2026-04-18 15:07:20'),
(47,5,'nefta','Marcaje','Marcaje','Descanso 1 marcado por Neftali Lopez','2026-04-18 15:07:28'),
(48,5,'nefta','Marcaje','Marcaje','Descanso 2 marcado por Neftali Lopez','2026-04-18 15:07:29'),
(49,5,'nefta','Marcaje','Marcaje','Salida marcada por Neftali Lopez','2026-04-18 15:07:30'),
(50,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 15:07:45'),
(51,2,'AnAm','Marcaje','Marcaje','Entrada marcada por Angel Amaliel','2026-04-18 15:08:06'),
(52,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 15:08:13'),
(53,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 16:04:14'),
(54,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 16:04:18'),
(55,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 16:05:39'),
(56,1,'admin','Solicitudes','Aprobar','Gestion id:3 Aprobar por RRHH','2026-04-18 16:05:52'),
(57,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 16:05:57'),
(58,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 16:16:08'),
(59,4,'marro','Cambio Turno','Solicitar','Cambio de turno solicitado por Nilson Marro | Turno nuevo id:4 | Area destino id:2','2026-04-18 16:17:31'),
(60,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 16:17:54'),
(61,1,'admin','Solicitudes','Aprobar','Cambio de turno id:2 Aprobado','2026-04-18 16:18:30'),
(62,4,'marro','Login','Login','Inicio de sesion: Nilson Marro','2026-04-18 16:18:35'),
(63,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 16:19:00'),
(64,2,'AnAm','Marcaje','Marcaje','Descanso 1 marcado por Angel Amaliel','2026-04-18 16:29:50'),
(65,2,'AnAm','Marcaje','Marcaje','Descanso 2 marcado por Angel Amaliel','2026-04-18 16:29:53'),
(66,2,'AnAm','Marcaje','Marcaje','Salida marcada por Angel Amaliel','2026-04-18 16:29:54'),
(67,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 16:30:05'),
(68,1,'admin','Empleados','Crear','Empleado creado: Eli | Area: 2','2026-04-18 16:54:29'),
(69,6,'eli','Login','Login','Inicio de sesion: Eli','2026-04-18 17:09:36'),
(70,2,'AnAm','Login','Login','Inicio de sesion: Angel Amaliel','2026-04-18 17:09:46'),
(71,6,'eli','Login','Login','Inicio de sesion: Eli','2026-04-18 17:09:56'),
(72,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 17:18:25'),
(73,6,'eli','Login','Login','Inicio de sesion: Eli','2026-04-18 17:30:21'),
(74,1,'admin','Login','Login','Inicio de sesion: Administrador RRHH','2026-04-18 17:33:29');

-- ============================================================
-- 11. BITACORA DETALLE — NUEVA TABLA
-- Registra el detalle de cada cambio:
-- Creación: valor_anterior = NULL
-- Modificación: ambos campos tienen valor
-- ============================================================
CREATE TABLE `bitacora_detalle` (
  `id_detalle`          bigint(20)   NOT NULL AUTO_INCREMENT,
  `id_log`              bigint(20)   NOT NULL,
  `id_usuario_afectado` int(11)      DEFAULT NULL,
  `campo_cambiado`      varchar(80)  COLLATE utf8mb4_spanish_ci NOT NULL,
  `valor_anterior`      varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `valor_nuevo`         varchar(300) COLLATE utf8mb4_spanish_ci DEFAULT NULL,
  `fecha_hora`          datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_detalle`),
  KEY `idx_bd_log`     (`id_log`),
  KEY `idx_bd_usuario` (`id_usuario_afectado`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- ============================================================
-- FOREIGN KEYS
-- ============================================================
ALTER TABLE `empleados`
  ADD CONSTRAINT `fk_emp_area`       FOREIGN KEY (`id_area`)          REFERENCES `areas`    (`id_area`),
  ADD CONSTRAINT `fk_emp_rol`        FOREIGN KEY (`id_rol`)           REFERENCES `roles`    (`id_rol`),
  ADD CONSTRAINT `fk_emp_turno`      FOREIGN KEY (`id_turno_default`) REFERENCES `turnos`   (`id_turno`),
  ADD CONSTRAINT `fk_emp_admin_area` FOREIGN KEY (`id_admin_area`)    REFERENCES `empleados`(`id_empleado`),
  ADD CONSTRAINT `fk_emp_creado_por` FOREIGN KEY (`creado_por`)       REFERENCES `empleados`(`id_empleado`),
  ADD CONSTRAINT `fk_emp_modif_por`  FOREIGN KEY (`modificado_por`)   REFERENCES `empleados`(`id_empleado`);

ALTER TABLE `marcajes`
  ADD CONSTRAINT `fk_marc_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados`(`id_empleado`);

ALTER TABLE `asignacion_turnos`
  ADD CONSTRAINT `fk_ast_emp`   FOREIGN KEY (`id_empleado`)    REFERENCES `empleados`(`id_empleado`),
  ADD CONSTRAINT `fk_ast_turno` FOREIGN KEY (`id_turno`)       REFERENCES `turnos`   (`id_turno`),
  ADD CONSTRAINT `fk_ast_admin` FOREIGN KEY (`id_admin_asigno`) REFERENCES `empleados`(`id_empleado`);

ALTER TABLE `solicitudes_gestion`
  ADD CONSTRAINT `fk_sg_emp`   FOREIGN KEY (`id_empleado`)       REFERENCES `empleados`   (`id_empleado`),
  ADD CONSTRAINT `fk_sg_tipo`  FOREIGN KEY (`id_tipo_gestion`)   REFERENCES `tipos_gestion`(`id_tipo_gestion`),
  ADD CONSTRAINT `fk_sg_admin` FOREIGN KEY (`id_admin_resolvio`) REFERENCES `empleados`   (`id_empleado`);

ALTER TABLE `solicitudes_cambio_turno`
  ADD CONSTRAINT `fk_sct_emp`          FOREIGN KEY (`id_empleado`)      REFERENCES `empleados`(`id_empleado`),
  ADD CONSTRAINT `fk_sct_turno_ini`    FOREIGN KEY (`id_turno_inicial`) REFERENCES `turnos`   (`id_turno`),
  ADD CONSTRAINT `fk_sct_turno_nvo`    FOREIGN KEY (`id_turno_nuevo`)   REFERENCES `turnos`   (`id_turno`),
  ADD CONSTRAINT `fk_sct_area_origen`  FOREIGN KEY (`id_area_origen`)   REFERENCES `areas`    (`id_area`),
  ADD CONSTRAINT `fk_sct_area_destino` FOREIGN KEY (`id_area_destino`)  REFERENCES `areas`    (`id_area`),
  ADD CONSTRAINT `fk_sct_rrhh`         FOREIGN KEY (`id_rrhh_resolvio`) REFERENCES `empleados`(`id_empleado`);

ALTER TABLE `bitacora`
  ADD CONSTRAINT `fk_bit_emp` FOREIGN KEY (`id_empleado`) REFERENCES `empleados`(`id_empleado`);

ALTER TABLE `bitacora_detalle`
  ADD CONSTRAINT `fk_bd_log`     FOREIGN KEY (`id_log`)              REFERENCES `bitacora` (`id_log`),
  ADD CONSTRAINT `fk_bd_usuario` FOREIGN KEY (`id_usuario_afectado`) REFERENCES `empleados`(`id_empleado`);

-- ============================================================
-- ÍNDICES ADICIONALES PARA REPORTES
-- ============================================================
CREATE INDEX idx_emp_admin_area ON empleados(id_admin_area);
CREATE INDEX idx_emp_creado_por ON empleados(creado_por);

-- ============================================================
-- VISTAS DE REPORTE
-- ============================================================

-- ¿Quién creó/modificó a cada empleado? ¿Cuál es su AdminArea?
CREATE OR REPLACE VIEW `v_empleados_detalle` AS
SELECT
    e.id_empleado, e.dpi, e.nombre_completo, e.usuario, e.correo,
    e.estado, e.dias_vacaciones, e.fecha_creacion,
    a.nombre_area, t.nombre_turno, r.nombre_rol,
    adm.nombre_completo AS nombre_admin_area,
    cre.nombre_completo AS creado_por_nombre,
    mdf.nombre_completo AS modificado_por_nombre
FROM empleados e
INNER JOIN areas    a    ON e.id_area          = a.id_area
INNER JOIN roles    r    ON e.id_rol           = r.id_rol
LEFT  JOIN turnos   t    ON e.id_turno_default = t.id_turno
LEFT  JOIN empleados adm ON e.id_admin_area    = adm.id_empleado
LEFT  JOIN empleados cre ON e.creado_por       = cre.id_empleado
LEFT  JOIN empleados mdf ON e.modificado_por   = mdf.id_empleado;

-- ¿Cuántos días laboró cada empleado? ¿A quién se le deben más días?
CREATE OR REPLACE VIEW `v_dias_laborados` AS
SELECT
    e.id_empleado, e.nombre_completo,
    a.nombre_area, t.nombre_turno,
    adm.nombre_completo              AS nombre_admin_area,
    COUNT(m.id_marcaje)              AS dias_laborados,
    SUM(CASE WHEN m.entrada_tarde=1 THEN 1 ELSE 0 END) AS dias_tarde
FROM empleados e
INNER JOIN areas     a   ON e.id_area          = a.id_area
LEFT  JOIN turnos    t   ON e.id_turno_default = t.id_turno
LEFT  JOIN empleados adm ON e.id_admin_area    = adm.id_empleado
LEFT  JOIN marcajes  m   ON e.id_empleado      = m.id_empleado
                        AND m.hora_salida IS NOT NULL
GROUP BY e.id_empleado, e.nombre_completo,
         a.nombre_area, t.nombre_turno, adm.nombre_completo
ORDER BY dias_laborados DESC;

-- ¿Cuántos empleados tiene cada AdminArea?
CREATE OR REPLACE VIEW `v_empleados_por_admin` AS
SELECT
    adm.id_empleado     AS id_admin,
    adm.nombre_completo AS nombre_admin,
    a.nombre_area, t.nombre_turno,
    COUNT(e.id_empleado) AS total_empleados
FROM empleados adm
INNER JOIN areas    a  ON adm.id_area          = a.id_area
LEFT  JOIN turnos   t  ON adm.id_turno_default = t.id_turno
LEFT  JOIN empleados e ON e.id_admin_area      = adm.id_empleado
                      AND e.estado             = 'Activo'
WHERE adm.id_rol = 2
GROUP BY adm.id_empleado, adm.nombre_completo, a.nombre_area, t.nombre_turno
ORDER BY a.nombre_area, t.nombre_turno, adm.nombre_completo;

-- Marcajes del día con detalle completo
CREATE OR REPLACE VIEW `v_marcajes_hoy` AS
SELECT
    m.id_marcaje, m.fecha_marcaje,
    m.hora_entrada, m.hora_descanso1, m.hora_descanso2, m.hora_salida,
    m.entrada_tarde,
    e.nombre_completo AS nombre_empleado,
    a.nombre_area, t.nombre_turno,
    t.hora_inicio AS turno_inicio, t.hora_fin AS turno_fin,
    adm.nombre_completo AS nombre_admin_area
FROM marcajes m
INNER JOIN empleados  e   ON m.id_empleado      = e.id_empleado
INNER JOIN areas      a   ON e.id_area          = a.id_area
LEFT  JOIN turnos     t   ON e.id_turno_default = t.id_turno
LEFT  JOIN empleados  adm ON e.id_admin_area    = adm.id_empleado
WHERE m.fecha_marcaje = CURDATE();

-- ============================================================
-- QUERIES DE REPORTE (ejemplos listos para usar en phpMyAdmin)
-- ============================================================
-- ¿Cuántos empleados tiene tal AdminArea?
--   SELECT * FROM v_empleados_por_admin WHERE id_admin = 2;
--
-- ¿Cuántos días laboró un empleado?
--   SELECT * FROM v_dias_laborados WHERE id_empleado = 3;
--
-- ¿A quién se le deben más días?
--   SELECT * FROM v_dias_laborados ORDER BY dias_laborados DESC LIMIT 10;
--
-- ¿Quién creó o modificó a un empleado?
--   SELECT * FROM v_empleados_detalle WHERE id_empleado = 3;
--
-- Marcajes de hoy completos:
--   SELECT * FROM v_marcajes_hoy ORDER BY nombre_area, nombre_turno;
--
-- Historial completo de cambios en bitácora:
--   SELECT b.fecha_hora, b.login, b.modulo, b.tipo_operacion,
--          b.descripcion, bd.campo_cambiado, bd.valor_anterior, bd.valor_nuevo
--   FROM bitacora b
--   LEFT JOIN bitacora_detalle bd ON b.id_log = bd.id_log
--   ORDER BY b.fecha_hora DESC;

SET FOREIGN_KEY_CHECKS = 1;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
