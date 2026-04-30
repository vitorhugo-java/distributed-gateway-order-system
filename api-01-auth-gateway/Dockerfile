# ============================================================
# JVM build
# ============================================================
FROM gradle:9.0.0-jdk21-alpine AS build
WORKDIR /app

COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY api-01-auth-gateway/build.gradle api-01-auth-gateway/build.gradle
COPY api-02-orders-crud/build.gradle  api-02-orders-crud/build.gradle

COPY api-01-auth-gateway/src/ api-01-auth-gateway/src/
COPY api-02-orders-crud/src/  api-02-orders-crud/src/

RUN gradle :api-01-auth-gateway:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/api-01-auth-gateway/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
