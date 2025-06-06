# Práctica 3 - Taller Final

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ibaimoya_Sistemas-Distribuidos&metric=bugs)](https://sonarcloud.io/component_measures?id=ibaimoya_Sistemas-Distribuidos&metric=bugs)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ibaimoya_Sistemas-Distribuidos&metric=code_smells)](https://sonarcloud.io/component_measures?id=ibaimoya_Sistemas-Distribuidos&metric=code_smells)
[![Duplicated Lines](https://sonarcloud.io/api/project_badges/measure?project=ibaimoya_Sistemas-Distribuidos&metric=duplicated_lines_density)](https://sonarcloud.io/component_measures?id=ibaimoya_Sistemas-Distribuidos&metric=duplicated_lines_density)

**Kinora** es una aplicación web para la gestión y descubrimiento de películas, desarrollada con **Spring Boot** (backend) y **React** (frontend). Permite a los usuarios explorar películas, gestionar favoritos, realizar valoraciones y administrar usuarios.

****

## Requisitos

| **Producto** | **Versión** | **Descarga** |
|:------:|:--------:|:------------:|
| Docker Desktop | 4.40.0+ | [link](https://www.docker.com/products/docker-desktop/) |
| Docker Compose | 2.27.0+ | [link](https://docs.docker.com/compose/install/#:~:text=Mac-,Windows,-Tip) |

> [!NOTE]  
> Para evitar problemas se recomienda que **Docker Desktop** esté abierto antes de comenzar la ejecución.

****


1. Clona o sitúate en la carpeta raíz de la práctica 3:
```
cd "Sistemas-Distribuidos/Práctica 3 - Taller Final/web"
```
2. Arranca todos los contenedores con Docker Compose:
```
docker-compose up --build
```

La aplicación estará disponible en:

- Frontend: http://localhost:8080
- Backend API: http://localhost:8080/api
- Base de datos: PostgreSQL en puerto 5432

****

## 🎬 Uso de la Aplicación

### Página de Bienvenida

- Accede a [http://localhost:8080](http://localhost:8080)
- Pantalla inicial con opciones para **Iniciar sesión** o **Registrarse**

### Registro de Usuario

- Haz clic en **"Registrarse"**
- Completa el formulario con:
  - Nombre de usuario
  - Correo electrónico
  - Contraseña
  - Confirmación de contraseña
- Haz clic en **"Crear cuenta"**

### Inicio de Sesión

- Haz clic en **"Iniciar sesión"**
- Introduce tus credenciales:
  - Nombre de usuario
  - Contraseña
- Haz clic en **"Iniciar sesión"**

### Explorar Películas

- Página principal: Muestra películas populares en formato grid
- Búsqueda: Utiliza la barra de búsqueda para encontrar películas específicas
- Scroll infinito: Desplázate hacia abajo para cargar más películas automáticamente

### Gestión de Favoritos

**Agregar a favoritos**:

- Pasa el cursor sobre una película
- Haz clic en el **corazón verde** para añadirla a favoritos

**Quitar de favoritos**:

- Haz clic en el **corazón rojo con X** para eliminarla

**Ver favoritos**:

- Accede al menú de usuario (esquina superior derecha)
- Selecciona **"Mis Películas"**

### Valoración de Películas

- Haz clic en cualquier película para ver sus detalles
- En la página de detalles, encuentra la sección **"Tu valoración"**
- Haz clic en las estrellas para valorar de 1 a 5
- La valoración se guarda automáticamente
- Puedes ver las estadísticas de la comunidad y la valoración de TMDB

### Visualización del Mapa

- En la página de detalles de la película
- Se muestra un mapa indicando el país de producción (si está disponible)

---

## 👑 Panel de Administración

### Acceso como Administrador

**Credenciales por Defecto**:

- Usuario: `admin`
- Contraseña: `admin`

> ⚠️ **Advertencia**: Solo para fines académicos/demo. En producción, cambiar estas credenciales inmediatamente.

### Acceso al Panel

- Inicia sesión con las credenciales de administrador
- En el menú de usuario aparecerá la opción **"Panel admin"**
- Haz clic para acceder al panel de administración

### Funcionalidades del Panel de Admin

#### 📊 Vista General

- Lista de usuarios: Visualiza todos los usuarios registrados (excepto administradores)
- Información mostrada: ID, nombre y email de cada usuario

#### 👤 Gestión de Usuarios

**Ver detalles de usuario**:

- Información básica del usuario
- Estadísticas de uso

**Ver películas favoritas**:

- Haz clic en **"Películas"** junto a cualquier usuario
- Se despliega la lista de películas favoritas
- Muestra: título, póster y valoración del usuario

**Eliminar usuarios**:

- Haz clic en el icono de papelera (🗑️)
- Confirma la eliminación en el diálogo
- ⚠️ Esta acción elimina también todos los datos asociados (favoritos, valoraciones)

---

## 🔍 Información Detallada

Para cada película favorita se muestra:

- Póster de la película (clickeable para ver detalles)
- Título de la película
- Valoración del usuario (1-5 estrellas o "-" si no ha valorado)

---

## 🚪 Navegación

- **Volver al inicio**: Haz clic en la flecha ← en la esquina superior izquierda
- **Cerrar sesión**: Utiliza el menú de usuario

---

## 🏗️ Arquitectura

### Backend (Spring Boot)

- Puerto: `8080`
- Base de datos: PostgreSQL
- API REST: Endpoints para gestión de usuarios, películas y favoritos
- Seguridad: Spring Security con autenticación basada en sesiones

### Frontend (React)

- Framework: React 18+ con TypeScript
- Estilado: Tailwind CSS
- Animaciones: Framer Motion
- Build: Vite

### Base de Datos

- PostgreSQL (contenedor Docker)
- Puerto: `5432`
- Esquemas: Usuarios, favoritos, valoraciones

### API Externa

- The Movie Database (TMDB): Para obtener información de películas
- Configuración: Variables de entorno `TMDB_API_KEY` o `TMDB_ACCESS_TOKEN`

---

## 🛑 Detener la Aplicación

Para detener todos los servicios:

```bash
docker-compose down
```

Para eliminar también los volúmenes (datos de la base de datos):

```bash
docker-compose down -v
```

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo `LICENSE` para más detalles.

---

## 👥 Autor

**Ibai Moya Aroz** – [GitHub](https://github.com/)
