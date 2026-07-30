-- =============================================================================
-- EN: Performance indexes for calendar_db.
--     Before this script every table only had its PRIMARY KEY, so every date
--     range query in the read path forced a full table scan (lunas alone holds
--     ~104.000 rows). Hibernate also creates these indexes automatically through
--     the @Index annotations on the entities when ddl-auto is set to "update",
--     but this script lets you apply them to an existing database without
--     restarting the application.
--
-- ES: Indices de rendimiento para calendar_db.
--     Antes de este script cada tabla solo tenia su PRIMARY KEY, por lo que cada
--     consulta por rango de fechas del camino de lectura provocaba un recorrido
--     completo de la tabla (solo lunas tiene ~104.000 filas). Hibernate tambien
--     crea estos indices automaticamente mediante las anotaciones @Index de las
--     entidades cuando ddl-auto vale "update", pero este script permite aplicarlos
--     sobre una base de datos existente sin reiniciar la aplicacion.
--
-- EN: Run with:  mysql -u root -p calendar_db < indexes.sql
-- ES: Ejecutar con:  mysql -u root -p calendar_db < indexes.sql
-- =============================================================================

USE calendar_db;

-- -----------------------------------------------------------------------------
-- EN: MySQL has no "CREATE INDEX IF NOT EXISTS", so this helper checks
--     information_schema first and makes the script safe to run repeatedly.
-- ES: MySQL no tiene "CREATE INDEX IF NOT EXISTS", asi que este procedimiento
--     auxiliar consulta primero information_schema y hace que el script se pueda
--     ejecutar varias veces sin error.
-- -----------------------------------------------------------------------------
DROP PROCEDURE IF EXISTS crear_indice_si_no_existe;

DELIMITER $$
CREATE PROCEDURE crear_indice_si_no_existe(
    IN nombre_tabla VARCHAR(64),
    IN nombre_indice VARCHAR(64),
    IN columnas VARCHAR(255))
BEGIN
    IF NOT EXISTS (SELECT 1
                     FROM information_schema.statistics
                    WHERE table_schema = DATABASE()
                      AND table_name = nombre_tabla
                      AND index_name = nombre_indice) THEN
        SET @ddl = CONCAT('CREATE INDEX ', nombre_indice,
                          ' ON ', nombre_tabla, ' (', columnas, ')');
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- -----------------------------------------------------------------------------
-- EN: lunas (~104.000 rows) - the hottest table of the read path.
--     idx_lunas_date               -> the +/- 14 month window around the request.
--     idx_lunas_nueva_date         -> "new moons since the last aponovo" counter.
--     idx_lunas_nueva_selecta_date -> aponovo counters and nearest aponovo lookup.
-- ES: lunas (~104.000 filas) - la tabla mas caliente del camino de lectura.
--     idx_lunas_date               -> la ventana de +/- 14 meses de la peticion.
--     idx_lunas_nueva_date         -> contador de "lunas nuevas desde el ultimo aponovo".
--     idx_lunas_nueva_selecta_date -> contadores de aponovos y busqueda del mas cercano.
-- -----------------------------------------------------------------------------
CALL crear_indice_si_no_existe('lunas', 'idx_lunas_date', '`date`');
CALL crear_indice_si_no_existe('lunas', 'idx_lunas_nueva_date', 'nueva, `date`');
CALL crear_indice_si_no_existe('lunas', 'idx_lunas_nueva_selecta_date', 'nueva, selecta, `date`');

-- -----------------------------------------------------------------------------
-- EN: eclipses (~9.900 rows). The read path always excludes partial and penumbral
--     eclipses, hence the composite indexes that start with those two flags.
-- ES: eclipses (~9.900 filas). El camino de lectura excluye siempre los eclipses
--     parciales y penumbrales, de ahi los indices compuestos que empiezan por esos
--     dos flags.
-- -----------------------------------------------------------------------------
CALL crear_indice_si_no_existe('eclipses', 'idx_eclipses_date', '`date`');
CALL crear_indice_si_no_existe('eclipses', 'idx_eclipses_visibles_date', 'es_parcial, es_penumbral, `date`');
CALL crear_indice_si_no_existe('eclipses', 'idx_eclipses_visibles_sol_date', 'es_parcial, es_penumbral, de_sol, `date`');

-- -----------------------------------------------------------------------------
-- EN: Remaining date-filtered tables.
-- ES: Resto de tablas filtradas por fecha.
-- -----------------------------------------------------------------------------
CALL crear_indice_si_no_existe('apo_peri_lunas', 'idx_apoperis_date', '`date`');
CALL crear_indice_si_no_existe('sye', 'idx_sye_date', '`date`');
CALL crear_indice_si_no_existe('metons', 'idx_metons_date', '`date`');
CALL crear_indice_si_no_existe('eclipenos', 'idx_eclipenos_date', '`date`');

-- -----------------------------------------------------------------------------
-- EN: Lookup columns used by the population job and the admin password check.
-- ES: Columnas de busqueda usadas por el proceso de poblacion y la comprobacion
--     de la contrasena de administrador.
-- -----------------------------------------------------------------------------
CALL crear_indice_si_no_existe('casaleros', 'idx_casaleros_eclipeno', 'eclipeno_id');
CALL crear_indice_si_no_existe('datos', 'idx_datos_concepto', 'concepto');

DROP PROCEDURE IF EXISTS crear_indice_si_no_existe;

-- -----------------------------------------------------------------------------
-- EN: Refresh the optimizer statistics so MySQL actually picks the new indexes.
-- ES: Refresca las estadisticas del optimizador para que MySQL use realmente los
--     nuevos indices.
-- -----------------------------------------------------------------------------
ANALYZE TABLE lunas, eclipses, apo_peri_lunas, sye, metons, eclipenos, casaleros, datos;
