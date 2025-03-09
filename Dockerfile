FROM openjdk:21

COPY ./target/adminV2.jar /app.jar

ENTRYPOINT ["/usr/bin/java", "-jar", "/app.jar"]
