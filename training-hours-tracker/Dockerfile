FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/training-hours-tracker-0.0.1-SNAPSHOT.jar training-hours-tracker.jar
COPY .env .env

ENV $(cat .env | xargs)

ENTRYPOINT ["java", "-jar", "training-hours-tracker.jar"]