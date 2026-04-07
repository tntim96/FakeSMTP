FROM eclipse-temurin:11-jdk

RUN mkdir -p /output

ADD https://github.com/tntim96/FakeSMTP/releases/download/v2.1.3-SNAPSHOT/fakeSMTP-2.1.3-SNAPSHOT.jar /

VOLUME /output

EXPOSE 25

ENTRYPOINT ["java","-jar","/fakeSMTP-2.1.3-SNAPSHOT.jar","--background", "--output-dir", "/output", "--port", "25", "--start-server"]
