# ALL-IN-ONE docker image
FROM maven:3.6.3-jdk-11 as builder

VOLUME /tmp

RUN mkdir /target
RUN apt-get update && apt-get install -y wget git && rm -rf /var/lib/apt/lists/*

# Get google or tools libs
RUN wget https://github.com/google/or-tools/releases/download/v7.5/or-tools_ubuntu-18.04_v7.5.7466.tar.gz
RUN tar -xzf or-tools_ubuntu-18.04_v7.5.7466.tar.gz
# move jni libs
RUN mkdir /jni
RUN cp -r or-tools_Ubuntu-18.04-64bit_v7.5.7466/lib/ /deps
# cleanup
RUN rm -r or-tools_Ubuntu-18.04-64bit_v7.5.7466

RUN mkdir /root/.ssh/
# Copy over private key, and set permissions
ADD id_rsa /root/.ssh/id_rsa
RUN chmod 700 /root/.ssh/id_rsa
RUN chown -R root:root /root/.ssh

# Create known_hosts
RUN touch /root/.ssh/known_hosts
# Add github key
RUN ssh-keyscan github.com >> /root/.ssh/known_hosts
RUN mkdir /repo
WORKDIR /repo

# Clone Repo
RUN git clone git@github.com:bananenhoschi/IP6-Optimal-Scheduling-of-Project-Presentations.git /repo

# Maven build
RUN mvn clean install -Pprod

# Copy runnable Spring Boot jar
RUN ls /repo/ospp-application/target/
RUN mv /repo/ospp-application/target/ospp-application-1.0-SNAPSHOT.jar /app.jar

FROM ubuntu:18.04
WORKDIR /
COPY --from=builder /app.jar /app.jar
COPY --from=builder /deps/* /usr/lib/

RUN apt-get update -y
RUN apt-get install openjdk-11-jdk -y

ENTRYPOINT ["java","-Dspring.profiles.active=dev","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
