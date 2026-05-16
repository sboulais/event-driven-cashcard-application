FROM ubuntu:latest
LABEL authors="sebastien.boulais@outlook.fr"
RUN apt-get update && apt-get install -y openjdk-17-jre-headless
COPY target/cashcard-enricher-0.0.1-SNAPSHOT-exec.jar /opt/enricher.jar
WORKDIR /opt
ENTRYPOINT ["java", "-jar", "enricher.jar"]