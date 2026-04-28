FROM gradle:8.10-jdk21-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY api-01-auth-gateway/ api-01-auth-gateway/
COPY api-02-orders-crud/ api-02-orders-crud/

RUN ./gradlew :api-02-orders-crud:bootJar -x test

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/api-02-orders-crud/build/libs/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]