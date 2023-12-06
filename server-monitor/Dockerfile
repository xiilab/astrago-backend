FROM openjdk:17

ENV LC_ALL=C.UTF-8

COPY build/libs/*.jar /java/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/java/app.jar"]
