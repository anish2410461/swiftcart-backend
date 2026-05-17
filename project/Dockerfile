FROM eclipse-temurin:17

WORKDIR /app

COPY . .

RUN chmod +x mvnw

RUN ./mvnw clean install -DskipTests=true

EXPOSE 8081

CMD ["java", "-jar", "target/Backend-0.0.1-SNAPSHOT.jar"]