# Use a local JDK image
FROM eclipse-temurin:17-jdk-jammy

# Copy the Spring Boot jar (skip tests when building jar)
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the jar
ENTRYPOINT ["java","-jar","/app.jar"]