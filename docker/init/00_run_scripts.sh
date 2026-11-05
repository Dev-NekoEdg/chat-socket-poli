#!/bin/bash
# ==========================================
# Carga inicial de la base de datos.
#
# Lo ejecuta el entrypoint de MySQL solo cuando la base de datos se crea
# por primera vez (carpeta docker/data/mysql vacía).
#
# Orden:
#   1. /scripts/DDL/*.sql  (tablas, índices, vistas, procedimientos...)
#   2. /scripts/DML/*.sql  (datos iniciales)
# Dentro de cada carpeta se ejecutan en orden alfabético, por eso se
# recomienda nombrarlos con prefijo numérico: 01_..., 02_..., etc.
#
# Si un script falla, la inicialización se detiene y el contenedor no
# arranca: revisar con "docker compose logs mysql".
# ==========================================

run_sql_folder() {
    local folder="$1"

    if [ ! -d "$folder" ]; then
        mysql_note "Carpeta $folder no encontrada, se omite."
        return
    fi

    local found=0
    while IFS= read -r file; do
        found=1
        mysql_note "Ejecutando $file"
        # docker_process_sql usa la base de datos MYSQL_DATABASE.
        docker_process_sql < "$file"
    done < <(find "$folder" -maxdepth 1 -type f -name '*.sql' | sort)

    if [ "$found" -eq 0 ]; then
        mysql_note "No hay archivos .sql en $folder"
    fi
}

run_sql_folder /scripts/DDL
run_sql_folder /scripts/DML
