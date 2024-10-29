# Empezamos con una imagen base que tiene Java instalado
FROM openjdk:11-jdk-slim

# Variables de entorno
#ARG JAR_FILE=./englishCard-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=target/*.jar
# Crear directorio app en el contenedor
WORKDIR /app

# Copiamos el archivo jar al directorio de trabajo en el contenedor
COPY ${JAR_FILE} app.jar

# Exponer el puerto en el que nuestra aplicación se ejecutará
EXPOSE 8080

# El comando para ejecutar la aplicación cuando se inicie el contenedor
ENTRYPOINT ["java","-jar","/app/app.jar"]
