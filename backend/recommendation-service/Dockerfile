# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .


COPY common-lib/pom.xml common-lib/
COPY common-lib/src common-lib/src

RUN mvn -N install && mvn -f common-lib/pom.xml install

COPY recommendation-service/pom.xml recommendation-service/
COPY recommendation-service/src recommendation-service/src

RUN mvn -f recommendation-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/recommendation-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]