FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=build /workspace/target/fusion-ia-api-*.jar app.jar

ENV SERVER_PORT=8000

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
