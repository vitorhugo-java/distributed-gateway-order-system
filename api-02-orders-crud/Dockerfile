FROM ghcr.io/graalvm/native-image-community:21 AS build-native
WORKDIR /app

RUN microdnf install -y findutils && microdnf clean all

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY api-01-auth-gateway/build.gradle api-01-auth-gateway/build.gradle
COPY api-02-orders-crud/build.gradle  api-02-orders-crud/build.gradle
COPY api-01-auth-gateway/src/ api-01-auth-gateway/src/
COPY api-02-orders-crud/src/  api-02-orders-crud/src/

RUN tr -d '\r' < gradlew > gradlew.unix && mv gradlew.unix gradlew && chmod +x gradlew \
    && ./gradlew :api-02-orders-crud:nativeCompile -x test --no-daemon \
        -Porg.gradle.java.installations.auto-download=false \
        -Porg.gradle.java.installations.auto-detect=false \
        -Porg.gradle.java.installations.paths="$JAVA_HOME"

FROM busybox:glibc AS native
WORKDIR /app

COPY --from=build-native \
     /app/api-02-orders-crud/build/native/nativeCompile/api-02-orders-crud \
     ./app

EXPOSE 8081
ENTRYPOINT ["/app/app"]
