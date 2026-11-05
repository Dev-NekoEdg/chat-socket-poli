# DDL

Scripts que crean los objetos de la base de datos: tablas, llaves, índices, vistas, procedimientos, triggers, etc.

- Se ejecutan **antes** que los de `DML/`.
- Se ejecutan en **orden alfabético**, así que conviene usar un prefijo numérico:
  `01_tables.sql`, `02_indexes.sql`, `03_procedures.sql`...
- Corren dentro de la base de datos `chat_socket_poli` (`MYSQL_DATABASE`), así que no se necesita `USE`.
- Solo se ejecutan cuando la base de datos se crea por primera vez. Para volver a ejecutarlos, ver la sección *Reiniciar la base de datos* del README principal.
- Para procedimientos o triggers, usar `DELIMITER` normalmente (se ejecutan con el cliente `mysql`).
