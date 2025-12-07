# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copy the parent pom
COPY pom.xml .
# Copy the source code
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy the jar from the build stage
# Note: This wildcard *.jar assumes only one jar is built
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]