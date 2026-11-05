# ==========================================
# Chat Socket Poli - Base de datos MySQL
# ==========================================
# MySQL 8.4 es la versión LTS (soporte a largo plazo).
FROM mysql:8.4

# Configuración del servidor y del cliente (utf8mb4, zona horaria).
COPY config/my.cnf /etc/mysql/conf.d/chat-socket-poli.cnf

# Script que ejecuta los archivos de DDL/ y luego los de DML/
# la PRIMERA vez que se crea la base de datos (data/mysql vacío).
COPY init/00_run_scripts.sh /docker-entrypoint-initdb.d/00_run_scripts.sh

# - sed: elimina los saltos de línea de Windows (CRLF), que rompen los archivos.
# - chmod 644: el script debe NO ser ejecutable para que el entrypoint lo
#   cargue con "source" y pueda usar la función docker_process_sql.
#   MySQL ignora archivos .cnf que cualquiera puede modificar, por eso 644.
RUN sed -i 's/\r$//' /docker-entrypoint-initdb.d/00_run_scripts.sh /etc/mysql/conf.d/chat-socket-poli.cnf \
    && chmod 644 /docker-entrypoint-initdb.d/00_run_scripts.sh /etc/mysql/conf.d/chat-socket-poli.cnf
