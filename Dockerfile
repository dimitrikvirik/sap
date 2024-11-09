FROM gradle:8-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle bootJar

FROM openjdk:23-slim
EXPOSE 8080

RUN mkdir /app
RUN apt-get update && apt-get install -y curl

COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]