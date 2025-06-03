FROM maven:3.9.0-eclipse-temurin-21 AS builder
WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:21-bullseye

WORKDIR /app
ARG JAR_FILE=target/myApp.jar
COPY ${JAR_FILE} myApp.jar

EXPOSE 8181
ENTRYPOINT ["java","-jar","/myApp.jar"]