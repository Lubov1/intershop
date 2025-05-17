FROM openjdk:21-bullseye

ARG JAR_FILE=target/myApp.jar
COPY ${JAR_FILE} myApp.jar

EXPOSE 8181
ENTRYPOINT ["java","-jar","/myApp.jar"]