FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline
COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget && addgroup -S app && adduser -S app -G app && mkdir -p /app/uploads/company-docs \
    && chown -R app:app /app
COPY --from=build /workspace/target/propex-0.0.1-SNAPSHOT.jar /app/app.jar
USER app
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 CMD wget -qO- http://127.0.0.1:8081/actuator/health >/dev/null || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
