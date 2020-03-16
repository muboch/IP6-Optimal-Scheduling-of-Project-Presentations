FROM java:8

VOLUME /tmp

EXPOSE 10222

ADD build/libs/osopp-1.0-SNAPSHOT.jar osopp-1.0-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","osopp-1.0-SNAPSHOT.jar"]