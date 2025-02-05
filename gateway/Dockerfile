FROM openjdk:17-jdk-slim
WORKDIR /app

COPY target/gateway-0.0.1-SNAPSHOT.jar gateway.jar

ENV $(cat .env | xargs)

ENTRYPOINT ["java", "-jar", "gateway.jar"]