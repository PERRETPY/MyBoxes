# Étape 1 : Image de base avec Java 21 (OpenJDK)
FROM eclipse-temurin:21-jdk-alpine

# Création d'un répertoire de travail
WORKDIR /app

# Copie du JAR dans l'image
COPY target/*.jar myboxes.jar

# Exposition du port (à adapter selon ton app)
EXPOSE 8080

# Lancement de l'application
ENTRYPOINT ["java", "-jar", "myboxes.jar"]
