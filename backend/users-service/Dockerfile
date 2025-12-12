# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .


COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src

RUN mvn -N install && mvn -f common-lib/pom.xml install

COPY users-service/pom.xml users-service/
COPY users-service/src users-service/src

RUN mvn -f users-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/users-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]