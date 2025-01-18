#OLtest
#Descripción del Proyecto

OLtest es un proyecto de prueba desarrollado en Spring Boot con REST APIs para la gestión de usuarios, departamentos, ciudades, comerciantes (merchants) y establecimientos (establishments). Incluye funcionalidades avanzadas como generación de reportes en Excel y PDF.

El proyecto está configurado para ejecutarse en un entorno Docker y utiliza una base de datos H2 en memoria. La autenticación se realiza mediante JWT (JSON Web Tokens).

#Requisitos Previos
Docker y Docker Compose instalados en tu máquina.
Cliente Postman o cualquier herramienta para consumir APIs REST.
Java 17 (en caso de ejecutar localmente sin Docker).

#Instrucciones de Ejecución

Paso 1: Construir y Levantar el Proyecto
Ejecuta el siguiente comando en la raíz del proyecto para construir y levantar los contenedores:
docker-compose up --build

Paso 2: Documentación de las APIs
Las APIs del proyecto están documentadas en una colección de Postman incluida en el repositorio. Asegúrate de importarla para probar fácilmente las funcionalidades.

Paso 3: Crear un Usuario
Para efectos de prueba, la API de creación de usuarios no requiere autenticación. Utiliza esta API para registrar un usuario inicial. Una vez creado, utiliza las credenciales del usuario para realizar el inicio de sesión.

Paso 4: Iniciar Sesión y Obtener el Token JWT
Usa la API de inicio de sesión para obtener un token JWT. Este token será necesario para autenticarte en las demás APIs del proyecto.

Tipo de autenticación: Bearer Token
Autorización: Authorization: Bearer xxxxxxx (Reemplaza xxxxxxx con el token obtenido).

Tip: Configura la variable del token JWT en la colección de postman para evitar agregarlo manualmente en cada petición.

#Orden Recomendado para Probar las APIs

* Crear Departamentos.
* Crear Ciudades asociadas a los departamentos.
* Crear Merchants (Comerciantes).
* Crear Establishments (Establecimientos) asociados a los comerciantes.

#Funcionalidades Principales

*CRUD Completo: Gestiona usuarios, departamentos, ciudades, comerciantes y establecimientos.
Autenticación JWT: Todas las APIs, excepto las de creación de usuario e inicio de sesión, requieren un token JWT válido.
Generación de Reportes:
*Excel: Genera reportes detallados de comerciantes en formato Excel.
*PDF: Genera reportes detallados de comerciantes en formato PDF.

#Base de Datos
La base de datos utilizada es H2, configurada para ejecutarse en memoria. Esto significa que:

Los datos se almacenan temporalmente mientras el contenedor está activo.
Al detener el contenedor, la base de datos se elimina automáticamente.

#Endpoints Destacados
* Usuarios
Crear Usuario: No requiere autenticación.
* Login: Devuelve un token JWT para autenticarse.
* Departamentos
CRUD completo para gestionar departamentos.
* Ciudades
CRUD completo para gestionar ciudades.
* Merchants (Comerciantes)
CRUD completo para gestionar comerciantes.
Generación de reportes en Excel y PDF.
* Establishments (Establecimientos)
CRUD completo para gestionar establecimientos.

#Tecnologías Utilizadas

-Java 17
-Spring Boot (Web, JPA, Security)
-Docker y Docker Compose
-H2 Database
-JWT para autenticación
-iText para generación de PDFs
-Postman para pruebas de APIs

