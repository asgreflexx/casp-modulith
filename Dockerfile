FROM openjdk:21

RUN touch /logback.xml

COPY ./target/adminV2.jar /admin-v2.jar

ENTRYPOINT ["/usr/bin/java", "-Dlogging.config=file:/logback.xml", "-jar", "/admin-v2.jar"]
