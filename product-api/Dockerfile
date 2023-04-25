FROM eclipse-temurin:17-alpine

WORKDIR /app

COPY ./target/product-api-0.0.1-SNAPSHOT.jar /app

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "product-api-0.0.1-SNAPSHOT.jar"]

CMD ["mvn", "spring-boot:run"]

