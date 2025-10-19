FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:resolve dependency:resolve-plugins

COPY src ./src

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]