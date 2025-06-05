# Compilación de React.
FROM node:20 AS react-builder

# ajusta si tu código React está en otro path
WORKDIR /frontend
COPY react-app/package*.json ./
RUN npm ci

COPY react-app/ ./
RUN npm run build

# Compilación de Java.
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

COPY --from=react-builder /frontend/dist ./src/main/resources/static

RUN mvn clean package -DskipTests

# Imagen final para ejecutar la aplicación Java.
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]