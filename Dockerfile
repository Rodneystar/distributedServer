FROM openjdk:11.0.6-jre-buster

COPY build/libs/distributedServer-0.0.1-SNAPSHOT.jar /app/distributedServer.jar

WORKDIR /app/

CMD ["java", "-jar", "distributedServer.jar"]