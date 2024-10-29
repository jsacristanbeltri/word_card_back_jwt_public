# English card backend - Spring Boot




Este proyecto es una aplicación Java Spring Boot que se ejecuta en un entorno de contenedores con Docker Compose.
Los servicios incluyen una base de datos PostgreSQL, un servicio de mensajería RabbitMQ, un servidor SMTP para correos, 
y herramientas de monitoreo como Grafana y Prometheus.

## Prerrequisitos

Antes de comenzar, asegúrate de tener instalados los siguientes programas en tu sistema:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

## Archivos de configuración

- **`.env.dev`**: contiene las variables de entorno para configurar los servicios, incluyendo la base de datos, RabbitMQ y el servidor de correos.
- **`docker-compose.yml`**: archivo de configuración de Docker Compose, que define los servicios necesarios.
- **`Dockerfile`**: archivo de configuración para construir la imagen de la aplicación Spring Boot.

## Configuración de Variables de Entorno

En el archivo `.env.dev`, se definen las variables de entorno para los servicios. Aquí un ejemplo de cómo debe verse:

```env
POSTGRES_DB=cardLanguage
SPRING_DATASOURCE_USERNAME=user_db
SPRING_DATASOURCE_PASSWORD=pass_db
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/cardLanguage

SPRING_RABBITMQ_USERNAME=rabbit_user
SPRING_RABBITMQ_PASSWORD=rabbit_pass
SPRING_RABBITMQ_HOST=rabbitmq
SPRING_RABBITMQ_PORT=5672

SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=ejemplo@gmail.com
SPRING_MAIL_PASS=xxx xxx xxx xxx
```
Nota:

Subtituye las variables que creas necesarias. Las de MAIL son obligatorias cambiarlas, para ello tendras que usar los datos de una cuenta de correo existente o crear una.

La configuración de correo usa una cuenta de Gmail; asegúrate de habilitar el acceso a aplicaciones menos seguras o configurar un "App Password" si tienes autenticación de dos factores habilitada.
# Levantar la Aplicación

Para poner en marcha la aplicación y sus servicios con Docker Compose, sigue los pasos a continuación:

## 1. Clonar el repositorio

Para comenzar, debes clonar el repositorio de GitHub a tu máquina local. Esto te permitirá obtener una copia completa del proyecto y trabajar con él.

#### Pasos para Clonar el Repositorio

1. **Obtener la URL del Repositorio**:

    - Navega al repositorio en GitHub.
    - Haz clic en el botón verde que dice **Code**.
    - Selecciona **HTTPS** y copia la URL que aparece. La URL tendrá un formato similar a este:
      ```plaintext
      https://github.com/tu-usuario/nombre-del-repositorio.git
      ```

2. **Abrir una Terminal**:

    - En tu computadora, abre una terminal o consola (en sistemas operativos como macOS y Linux) o el Símbolo del sistema o PowerShell (en Windows).

3. **Ejecutar el Comando de Clonado**:

    - Cambia el directorio a la ubicación donde deseas clonar el repositorio.
    - Usa el siguiente comando para clonar el repositorio:
      ```bash
      git clone https://github.com/tu-usuario/nombre-del-repositorio.git
      ```

    - Asegúrate de reemplazar `https://github.com/tu-usuario/nombre-del-repositorio.git` con la URL específica de tu repositorio.

4. **Acceder al Directorio del Proyecto**:

    - Una vez que el proceso de clonación haya finalizado, navega al directorio del proyecto clonado con:
      ```bash
      cd nombre-del-repositorio
      ```

## 2. Construir el proyecto

Al usar Maven , debemos ejectuar el siguiente comando para crear el archivo .jar. 
```bash
mvn clean install
```
Esto generará un archivo .jar en la carpeta target del proyecto, el cual será utilizado en el contenedor.

## 3. Levantar los contenedores


Ejecuta el siguiente comando para levantar todos los servicios definidos en docker-compose.yml:

```bash
docker-compose --env-file .env.dev up -d --build
```

Esto:

Construirá la imagen de la aplicación Java Spring Boot.
Creará y levantará los contenedores de PostgreSQL, RabbitMQ, Grafana, Prometheus y Spring Boot.

### Nota:

Inicialización de la Base de Datos: El archivo init.sql se encuentra mapeado a la base de datos PostgreSQL y se ejecutará al inicializar el contenedor de la base de datos.

Credenciales de Acceso:

RabbitMQ: usa las credenciales rabbit_user y rabbit_pass para acceder a la interfaz de administración de RabbitMQ.



## 4. Verificar que los contenedores estén corriendo

Puedes verificar el estado de los contenedores con:
```bash
docker-compose ps
```

# Detener la aplicacion

Para detener y eliminar todos los contenedores, ejecuta:
```bash
docker-compose down
```
Si deseas eliminar también las redes y volúmenes, utiliza:
```bash
docker-compose down --volumes --remove-orphans
```

# Endpoints
Este proyecto utiliza **Swagger** para generar documentación interactiva de los endpoints.

### Acceso a Swagger UI

Una vez que el proyecto esté en ejecución, puedes acceder a la documentación de la API en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).


## Monitorización de la Aplicación con Prometheus

La aplicación incluye **Prometheus** como sistema de monitorización para recopilar métricas de rendimiento. Prometheus se ejecuta en un contenedor Docker y se configura mediante `docker-compose.yml` para recolectar y almacenar métricas, que pueden visualizarse y analizarse mediante Grafana.

### ¿Qué es Prometheus?

[Prometheus](https://prometheus.io/) es una herramienta de monitorización de código abierto que permite recopilar y consultar métricas de aplicaciones y sistemas. Es útil para supervisar la salud de la aplicación, identificar problemas de rendimiento y analizar patrones de uso.

### Configuración de Prometheus

El archivo `docker-compose.yml` incluye la configuración de Prometheus. A continuación, se muestra un extracto relevante:

```yaml
services:
  prometheus:
    image: prom/prometheus
    container_name: prometheus
    volumes:
      - "./prometheus.yml:/etc/prometheus/prometheus.yml"
    ports:
      - "9090:9090"
```
El archivo prometheus.yml es donde se define la configuración de Prometheus, incluyendo los targets o puntos finales de las aplicaciones que Prometheus monitoreará.

### Acceso a la interfaz prometheus

Una vez que hayas levantado los servicios con docker-compose, puedes acceder a la interfaz de Prometheus en:
http://localhost:9090

## Grafana

Grafana está incluido en docker-compose.yml para visualizar métricas de Prometheus y crear dashboards interactivos. Aquí se explica cómo conectar Prometheus en Grafana:

- Accede a Grafana en http://localhost:3000.

- Inicia sesión en Grafana (por defecto, el usuario y contraseña son admin/admin).

- Navega a Configuration > Data Sources y selecciona Add data source.

- Selecciona Prometheus como fuente de datos e introduce la URL de Prometheus (http://prometheus:9090).

- Haz clic en Save & Test para verificar la conexión.

## Seguridad en la Aplicación: Spring Security y JWT

Este proyecto implementa la seguridad a través de **Spring Security** y utiliza **JSON Web Tokens (JWT)** para la autenticación y autorización de los usuarios. JWT es un mecanismo seguro para transmitir información entre el cliente y el servidor, permitiendo gestionar sesiones sin mantener datos en el servidor.

### ¿Qué es JWT?

[JWT (JSON Web Token)](https://jwt.io/) es un estándar de token de acceso seguro que permite transmitir información de manera confiable. Cada token está compuesto por tres partes:
1. **Header**: Contiene el tipo de token y el algoritmo de cifrado.
2. **Payload**: Contiene los datos de usuario y cualquier otra información relevante.
3. **Signature**: Garantiza que el token no ha sido alterado.

### Flujo de Autenticación en el Proyecto

1. **Inicio de Sesión**:
    - El usuario envía sus credenciales (nombre de usuario y contraseña) al endpoint de autenticación: /api/v1/auth/login
   ```yaml
      {
      "password": "string",
      "username": "string"
      }
   ```
    - Spring Security valida las credenciales y, si son correctas, genera un JWT.

2. **Generación del Token**:
    - El JWT generado se devuelve al cliente y contiene información sobre el usuario y su rol.
    - El cliente debe incluir este token en el encabezado `Authorization` (como `Bearer <token>`) en todas las solicitudes posteriores a los endpoints protegidos.

3. **Autorización**:
    - Cada solicitud a un endpoint protegido es interceptada por Spring Security.
    - Spring Security verifica el token JWT y, si es válido, concede acceso al recurso.

