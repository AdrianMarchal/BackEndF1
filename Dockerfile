FROM amazoncorretto:21-alpine-jdk

COPY target/BackEndF1-0.0.1-SNAPSHOT.jar /api-v1.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/api-v1.jar"]