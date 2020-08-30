FROM openjdk:8-alpine

COPY target/kaspro-*.jar /kaspro.jar

CMD ["java", "-jar", "/kaspro.jar"]