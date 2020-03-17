# Maven build container

FROM maven:3.5.2-jdk-8-alpine AS maven_build

COPY pom.xml /tmp/

COPY src /tmp/src/

WORKDIR /tmp/

RUN mvn package

#pull base image

FROM openjdk:8-jdk-alpine

#maintainer
MAINTAINER carlo.eggenschiler@students.fhnw.ch

#expose port 8080
EXPOSE 4345

#default command
CMD java -jar /data/app.jar

#copy jar to docker image from builder image
COPY --from=maven_build /tmp/target/ospp*.jar /data/app.jar