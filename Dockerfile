FROM eclipse-temurin:21

COPY ./target/adminV2.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
