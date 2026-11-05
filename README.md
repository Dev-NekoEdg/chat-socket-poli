# Chat socket poli

Chat utilizando sockets.

Proyecto realizado para **Persistencia y datos transaccionales**.

Autores:
Edgard Alvarado Angulo - ecaalvaradoa@poligran.edu.co


----
## Proyecto:

#### Antes de iniciar:

Antes de poder trabajar y poder ejecutar el proyecto de manera correcta, se debe de tener los siguientes componentes:


* Visual Studio Code
    * VS Code extension: [Extension pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
    * VS Code extension: [Debugger For Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)
    * VS Code extension: [Maven For Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)
* Java 25 LTS (JDK 25.0.5 or newer)
* [Maven 3.10.0](https://maven.apache.org/download.cgi#CurrentMaven)
* Docker

#### Base de datos (MySQL en Docker)

La base de datos está en la carpeta `docker/`:

```
docker/
├── Dockerfile            # Imagen MySQL 8.4 LTS
├── docker-compose.yml    # Servicio, puertos, volúmenes y credenciales
├── .env.example          # Valores que se pueden cambiar (copiar como .env)
├── config/my.cnf         # utf8mb4 y zona horaria -05:00
├── init/                 # Script que ejecuta DDL/ y luego DML/
├── DDL/                  # Scripts de creación de objetos (tablas, índices, procedimientos...)
├── DML/                  # Scripts de carga inicial de datos
└── data/                 # Datos de MySQL (no se suben a git)
```

Levantar la base de datos (con Docker Desktop abierto):

```bash
cd docker
docker compose up -d --build
```

La primera vez se crea la base de datos y se ejecutan, en orden alfabético, todos los `.sql` de `DDL/` y después todos los de `DML/`.

| Dato | Valor por defecto |
|---|---|
| Host / puerto | `localhost:3306` |
| Base de datos | `chat_socket_poli` |
| Usuario / contraseña | `chat_user` / `chat_pass_2026` |
| Usuario root | `root` / `root_poli_2026` |

Para cambiarlos (por ejemplo, si el puerto 3306 ya está en uso), copiar `docker/.env.example` como `docker/.env` y editarlo.

Otros comandos útiles (desde `docker/`):

* `docker compose logs -f mysql`: ver los logs (aquí aparece cada script ejecutado o el error si alguno falla).
* `docker compose down`: detener la base de datos. Los datos se conservan en `docker/data/`.
* `docker exec -it chat-socket-poli-mysql mysql -uchat_user -p chat_socket_poli`: abrir una consola SQL.

##### Reiniciar la base de datos

Los scripts de `DDL/` y `DML/` **solo se ejecutan cuando `docker/data/mysql` está vacío**. Para volver a ejecutarlos (por ejemplo, después de cambiar un script), se borran los datos y se levanta de nuevo:

```bash
cd docker
docker compose down
# PowerShell: Remove-Item -Recurse -Force data/mysql
rm -rf data/mysql
docker compose up -d --build
```

> ⚠️ Esto elimina todos los datos guardados (mensajes, chatrooms, etc.).

#### Correr proyecto

0. Levantar la base de datos (ver sección anterior).
1. Desde `chat-socket-poli`, En una terminal, ejecutar el comando `mvn clean install` para validar que todo esté bien
2. Se hace el cambio al folder de `server` con el comando `cd server` y se ejecuta el comando `mvn exec:java`.
3. Con el servidor levantado se puede levantar el servidor de **UI**, en una nueva terminal desde `chat-socket-poli`, se hace el cambio al folder de `client` ejecutando `cd client` y luego ejecutando el comando `mvn javafx:run`.
    * Para probar con multilpes usuarios, es necesario repetir el paso **3**. O sea, abrir una nueva terminal, cambiar al folder de `client` y ejecutar el comando de `mvn javafx:run`.

