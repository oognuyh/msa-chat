FROM openjdk:11.0.7-jre-slim-buster
COPY build/libs/*.jar service.jar
ENTRYPOINT [ "java", "-Xmx200m", "-jar", "service.jar" ]

