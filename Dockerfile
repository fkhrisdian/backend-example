FROM openjdk:8-alpine

COPY target/sample-*.jar /sample.jar

CMD ["java", "-jar", "/sample.jar"]