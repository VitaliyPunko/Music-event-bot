FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

COPY build/libs/music-event-bot.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]


