# Usa Maven con JDK 17
FROM maven:3.8.8-eclipse-temurin-17 AS builder

# Establece el directorio de trabajo
WORKDIR /app

# Copia el proyecto al contenedor
COPY . .

# Compila el proyecto con Maven, omitiendo las pruebas
RUN mvn clean package -DskipTests

# Usa una imagen ligera de Java 17 para ejecutar el proyecto
FROM eclipse-temurin:17-jdk

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo JAR generado en la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto 8080
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
