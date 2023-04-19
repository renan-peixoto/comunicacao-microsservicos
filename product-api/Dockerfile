FROM eclipse-temurin:17-jdk
COPY --chown=maven:maven . .
WORKDIR .
RUN mvn install -DskipTests
COPY . .
EXPOSE 8081 
CMD ["mvn", "spring-boot:run"]
