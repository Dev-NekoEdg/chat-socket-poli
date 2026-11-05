# DML

Scripts con la carga inicial de datos (`INSERT`): usuarios de prueba, chatrooms, mensajes, catálogos, etc.

- Se ejecutan **después** de todos los de `DDL/`.
- Se ejecutan en **orden alfabético**, así que conviene usar un prefijo numérico:
  `01_users.sql`, `02_chatrooms.sql`, `03_messages.sql`...
- Corren dentro de la base de datos `chat_socket_poli` (`MYSQL_DATABASE`), así que no se necesita `USE`.
- Solo se ejecutan cuando la base de datos se crea por primera vez. Para volver a ejecutarlos, ver la sección *Reiniciar la base de datos* del README principal.
