# ============================================================
# Shared dependency-resolution layer (JVM builds)
# ============================================================
FROM gradle:8.10-jdk21-alpine AS gradle-deps
WORKDIR /app

COPY gradlew .
RUN chmod +x gradlew
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY api-01-auth-gateway/build.gradle api-01-auth-gateway/build.gradle
COPY api-02-orders-crud/build.gradle  api-02-orders-crud/build.gradle

# Warm up the Gradle dependency cache; failures are non-fatal
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# ============================================================
# TARGET: jvm — local development (fast JVM build)
# ============================================================
FROM gradle-deps AS build-jvm

COPY api-01-auth-gateway/src/ api-01-auth-gateway/src/
COPY api-02-orders-crud/src/  api-02-orders-crud/src/

RUN ./gradlew :api-01-auth-gateway:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine AS jvm
WORKDIR /app

COPY --from=build-jvm /app/api-01-auth-gateway/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# ============================================================
# TARGET: native — production (GraalVM native binary)
# ============================================================
FROM ghcr.io/graalvm/native-image-community:21 AS build-native
WORKDIR /app

RUN microdnf install -y findutils && microdnf clean all

COPY gradlew .
RUN chmod +x gradlew
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY api-01-auth-gateway/build.gradle api-01-auth-gateway/build.gradle
COPY api-02-orders-crud/build.gradle  api-02-orders-crud/build.gradle
COPY api-01-auth-gateway/src/ api-01-auth-gateway/src/
COPY api-02-orders-crud/src/  api-02-orders-crud/src/

RUN ./gradlew clean :api-01-auth-gateway:nativeCompile -x test --no-daemon \
    -Porg.gradle.java.installations.auto-download=false \
    -Porg.gradle.java.installations.auto-detect=false \
    -Porg.gradle.java.installations.paths="$JAVA_HOME"

FROM debian:bookworm-slim AS native
WORKDIR /app

RUN apt-get update && apt-get install -y zlib1g && rm -rf /var/lib/apt/lists/*

COPY --from=build-native \
     /app/api-01-auth-gateway/build/native/nativeCompile/api-01-auth-gateway \
     ./app-exe

RUN chmod +x ./app-exe

EXPOSE 8080
ENTRYPOINT ["./app-exe"]
