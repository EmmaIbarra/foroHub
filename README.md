# 🛡️ ForoHub API

## Índice
1. [Descripción](#descripción)
2. [Funcionalidades](#funcionalidades)
3. [Requisitos previos](#requisitos-previos)
4. [Cómo usar la API](#cómo-usar-la-api)
5. [Métodos de la API](#métodos-de-la-api)
    - [Gestión de Tópicos](#gestión-de-tópicos)
    - [Gestión de Usuarios](#gestión-de-usuarios)
    - [Autenticación](#autenticación)

## Descripción
**ForoHub** es una API diseñada para manejar tópicos y usuarios en un foro, proporcionando funcionalidades robustas para registro, actualización, eliminación y consulta. Incluye un sistema de seguridad basado en **Spring Security** con autenticación mediante **JWT (JSON Web Tokens)**.

Los endpoints están protegidos, por lo que los usuarios deben autenticarse para interactuar con ellos. Se recomienda utilizar herramientas como **Postman** o **Insomnia** para realizar pruebas.

## Funcionalidades
- **Gestión de tópicos:**
    - Crear, listar, actualizar y eliminar tópicos.
    - Consultar tópicos específicos por ID.
- **Gestión de usuarios:**
    - Crear, listar, actualizar y eliminar usuarios.
    - Gestionar perfiles asociados a usuarios.
- **Autenticación y seguridad:**
    - Login mediante email y contraseña.
    - Acceso seguro mediante tokens JWT.

## Requisitos previos
1. **Base de datos:** Configura una base de datos y las variables de entorno necesarias:
    - **DB_URL:** para la url de tu base de datos.
    - **DB_USERNAME:** para el usuario de tu base de datos.
    - **DB_PASSWORD:** para la contraseña de tu base de datos.
    - **JWT_SECRET:** esta contraseña será utilizada para acceder a las funcionalidades de la API.

2. **Dependencias necesarias:**
    - Java 17 o superior.
    - Spring Boot.
    - PostgreSQL o la base de datos que prefieras configurar.

3. **Herramientas sugeridas para pruebas:**
    - **Postman** o **Insomnia**.

## Cómo usar la API

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/EmmaIbarra/foroHub.git
   ```

2. **Configura las variables de entorno:**
   En el archivo pom.xml se encuentran las configuraciones para la base de datos que previamente se especificaron en los requisitos previos:
```properties
   spring.datasource.url=${DB_URL}
   spring.datasource.username=${DB_USERNAME}
   spring.datasource.password=${DB_PASSWORD}
   api.security.secret=${JWT_SECRET}
   ```
3. **Ejecuta la aplicación:**
   ```bash
   ./mvnw spring-boot:run
   ```
4. **Creación de usuario:** Una vez que la aplicación se inició y se crearon las tablas en la base de datos, es necesario crear manualmente el usuario con el que se realizarán todas las pruebas.
Para ello en la tabla usuarios, se debe completar con: 
   - **Nombre** 
   - **Email**
   - **Contraseña de la variable de entorno JWT_SECRET.** Es importante recalcar que para aumentar la seguridad, la contraseña debe ser almacenada utilizando un algoritmo de hash seguro, como bcrypt.
   
   Aquí el enlace de una pagina para realizarlo:
   ```
   https://bcrypt.online/
   ```

5. **Prueba los endpoints:**
   Usa una herramienta como **Postman** o **Insomnia** para interactuar con la API.

## Métodos de la API

### Gestión de Tópicos

#### Crear un tópico
- **Endpoint:** `POST /topicos`
- **Descripción:** Registra un nuevo tópico en el foro.
- **Cuerpo de la petición:**
  ```json
  {
    "titulo": "Ejemplo de Tópico",
    "mensaje": "Este es un mensaje de ejemplo.",
    "autor": "Nombre del Autor",
    "curso": "Nombre del Curso"
  }
  ```
- **Respuesta exitosa:** Código 201 y los datos del tópico creado.

#### Listar tópicos
- **Endpoint:** `GET /topicos`
- **Descripción:** Devuelve una lista paginada de tópicos ordenados por fecha de creación.
- **Parámetros opcionales:**
    - `page`: Número de página.
    - `size`: Tamaño de la página.

#### Consultar un tópico por ID
- **Endpoint:** `GET /topicos/{id}`
- **Descripción:** Devuelve los detalles de un tópico específico.

#### Actualizar un tópico
- **Endpoint:** `PUT /topicos/{id}`
- **Descripción:** Actualiza un tópico existente.
- **Cuerpo de la petición:**
  ```json
  {
    "titulo": "Nuevo Título",
    "mensaje": "Nuevo mensaje."
  }
  ```

#### Eliminar un tópico
- **Endpoint:** `DELETE /topicos/{id}`
- **Descripción:** Elimina un tópico por su ID.

### Gestión de Usuarios

#### Crear un usuario
- **Endpoint:** `POST /usuarios`
- **Descripción:** Registra un nuevo usuario.
- **Cuerpo de la petición:**
  ```json
  {
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "clave": "password123(hasheado)"
  }
  ```
- **Respuesta exitosa:** Código 201 y los datos del usuario creado.

#### Listar usuarios
- **Endpoint:** `GET /usuarios`
- **Descripción:** Devuelve una lista paginada de usuarios ordenados por nombre.

#### Consultar un usuario por ID
- **Endpoint:** `GET /usuarios/{id}`
- **Descripción:** Devuelve los detalles de un usuario específico.

#### Actualizar un usuario
- **Endpoint:** `PUT /usuarios/{id}`
- **Descripción:** Actualiza un usuario existente.
- **Cuerpo de la petición:**
  ```json
  {
    "nombre": "Nuevo Nombre",
    "email": "nuevoemail@example.com"
  }
  ```

#### Eliminar un usuario
- **Endpoint:** `DELETE /usuarios/{id}`
- **Descripción:** Elimina un usuario por su ID.

#### Gestión de perfiles
- **Agregar perfil:** `POST /usuarios/{id}/perfiles`
- **Eliminar perfil:** `DELETE /usuarios/{id}/perfiles/{perfilId}`
- **Listar perfiles:** `GET /usuarios/{id}/perfiles`

### Autenticación

#### Login
- **Endpoint:** `POST /login`
- **Descripción:** Autentica al usuario y genera un token JWT.
- **Cuerpo de la petición:**
  ```json
  {
    "email": "juan@example.com",
    "clave": "password123(hasheado)"
  }
  ```
- **Respuesta exitosa:**
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR..."
  }
  ```

## Notas importantes
- **Seguridad:** Todos los endpoints (excepto `/login`) requieren un token JWT válido, el cual tiene una duración de **dos horas**, después de este tiempo debe volver a ser generado.
- **Pruebas:** Usa herramientas como **Postman** para incluir el token JWT en los encabezados de autorización:
  ```plaintext
  Authorization: Bearer <TOKEN>
  
