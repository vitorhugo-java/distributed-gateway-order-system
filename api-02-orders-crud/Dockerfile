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

RUN ./gradlew clean :api-02-orders-crud:nativeCompile -x test --no-daemon \
    -Porg.gradle.java.installations.auto-download=false \
    -Porg.gradle.java.installations.auto-detect=false \
    -Porg.gradle.java.installations.paths="$JAVA_HOME"

FROM debian:bookworm-slim AS native
WORKDIR /app

RUN apt-get update && apt-get install -y zlib1g && rm -rf /var/lib/apt/lists/*

COPY --from=build-native \
     /app/api-02-orders-crud/build/native/nativeCompile/api-02-orders-crud \
     ./app-exe

RUN chmod +x ./app-exe

EXPOSE 8081
ENTRYPOINT ["./app-exe"]
