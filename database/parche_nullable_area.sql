-- ============================================================
-- PARCHE — id_area nullable para AdminRRHH
-- Ejecutar en phpMyAdmin sobre la BD control_turnos
-- ============================================================

USE control_turnos;

-- 1. Quitar el NOT NULL de id_area en empleados
--    AdminRRHH puede no tener área asignada
ALTER TABLE empleados
  MODIFY COLUMN `id_area` int(11) DEFAULT NULL;

-- 2. Quitar el NOT NULL de id_turno_default también
--    AdminRRHH puede no tener turno asignado
ALTER TABLE empleados
  MODIFY COLUMN `id_turno_default` int(11) DEFAULT NULL;

-- 3. Verificar que los datos existentes siguen correctos
SELECT id_empleado, nombre_completo, id_rol, id_area, id_turno_default
FROM empleados
ORDER BY id_rol, nombre_completo;
