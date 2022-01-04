FROM openjdk:11

RUN apt-get update && apt-get install -y \
  gcc \
  build-essential \
  && rm -rf /var/lib/apt/lists/*

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
COPY judgeData ./judgeData
ENTRYPOINT ["java","-jar","/app.jar"]