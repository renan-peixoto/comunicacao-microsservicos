FROM maven:3.8.1-openjdk-17-slim as build

COPY /src /app/src
COPY .mvn .mvn
COPY /pom.xml /app


RUN mvn -f /app/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
EXPOSE 8081

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
