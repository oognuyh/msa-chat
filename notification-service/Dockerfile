# FROM openjdk:11.0.7-jdk-slim-buster as builder
FROM gradle:7.2.0-jdk11-alpine AS builder
WORKDIR /
COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src
RUN gradle bootJar

FROM openjdk:11.0.7-jre-slim-buster
COPY --from=builder /build/libs/*.jar service.jar
ENTRYPOINT [ "java", "-Xmx200m", "-jar", "service.jar" ]
