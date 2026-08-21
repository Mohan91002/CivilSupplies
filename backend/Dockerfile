# ---- Build stage ----
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN apk add --no-cache maven \
    && mvn -B -q -DskipTests package \
    && mv target/*.jar app.jar

# ---- Runtime stage ----
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S appgrp && adduser -S appuser -G appgrp
COPY --from=build /workspace/app.jar app.jar

RUN mkdir -p /app/uploads && chown -R appuser:appgrp /app
USER appuser

ENV APP_PROFILE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
