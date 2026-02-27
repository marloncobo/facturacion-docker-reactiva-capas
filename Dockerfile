# Etapa 1: Compilación (Usa una imagen con Maven)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila el proyecto dentro de Docker
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Imagen ligera solo con Java)
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copia el JAR generado en la etapa 1
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]