FROM openjdk:21-jdk-slim-buster

WORKDIR /app

COPY build/libs/gansik-shop-0.0.1-SNAPSHOT.jar /app/myapp.jar

CMD ["java", "-jar", "/app/myapp.jar"]
