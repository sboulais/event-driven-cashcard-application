FROM ubuntu:latest
LABEL authors="sebastien.boulais@outlook.fr"
RUN apt-get update && apt-get install -y openjdk-17-jre-headless
COPY target/cashcard-sink-0.0.1-SNAPSHOT-exec.jar /opt/sink.jar
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "sink.jar"]