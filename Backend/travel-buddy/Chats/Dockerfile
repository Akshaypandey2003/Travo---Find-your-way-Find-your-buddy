# syntax=docker/dockerfile:1
################################################################################
# Stage: deps - download dependencies (uses BuildKit cache for ~/.m2)
FROM eclipse-temurin:21-jdk-jammy AS deps
WORKDIR /build

# Copy mvnw wrapper and .mvn and pom.xml only — keeps this layer cacheable
COPY mvnw ./
COPY .mvn/ .mvn/
COPY pom.xml ./pom.xml

# Ensure wrapper is executable and create m2 folder
RUN chmod +x ./mvnw && mkdir -p /root/.m2 && chmod -R 777 /root/.m2

# Download dependencies into cache
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests dependency:go-offline

################################################################################
# Stage: package - build the app (we copy sources now)
FROM deps AS package
WORKDIR /build

# Copy the full source after dependencies were downloaded so source changes won't bust dependency cache.
COPY . .

# Build package and robustly locate the produced jar, then copy it to target/app.jar
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -DskipTests package && \
    echo "== listing target folder (inside docker build) ==" && ls -la target && \
    JAR=$(ls target/*.jar 2>/dev/null | grep -v '\-sources\.jar' | grep -v '\-javadoc\.jar' | head -n 1) && \
    if [ -z "$JAR" ]; then echo "ERROR: no jar found in target/"; exit 1; fi && \
    echo "Using jar: $JAR" && \
    cp "$JAR" target/app.jar

################################################################################
# Stage: extract - use Spring Boot layered jar tooling
FROM package AS extract
WORKDIR /build
RUN java -Djarmode=layertools -jar target/app.jar extract --destination target/extracted

################################################################################
# Stage: final - minimal runtime image
FROM eclipse-temurin:21-jre-jammy AS final

# Create non-root user
ARG UID=10001
RUN adduser --disabled-password --gecos "" --home "/nonexistent" --shell "/sbin/nologin" --no-create-home --uid "${UID}" appuser
USER appuser

WORKDIR /app

# Copy extracted layers
COPY --from=extract /build/target/extracted/dependencies/ ./
COPY --from=extract /build/target/extracted/spring-boot-loader/ ./
COPY --from=extract /build/target/extracted/snapshot-dependencies/ ./
COPY --from=extract /build/target/extracted/application/ ./

EXPOSE 8092

ENTRYPOINT ["java","org.springframework.boot.loader.launch.JarLauncher"]
