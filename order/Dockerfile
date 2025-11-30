FROM eclipse-temurin:23-jdk-ubi9-minimal
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8898
ENTRYPOINT ["java", "-jar", "app.jar"]