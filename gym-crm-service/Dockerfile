FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/gym-crm-service-0.0.1-SNAPSHOT.jar gym-crm-service.jar
COPY .env .env

ENV $(cat .env | xargs)

ENTRYPOINT ["java", "-jar", "gym-crm-service.jar"]