FROM openjdk:17-jdk-alpine

ENV TZ=Asia/Shanghai

RUN apk update && \
    apk add --no-cache tzdata && \
    rm -rf /var/cache/apk/*

WORKDIR /app

COPY target/library-0.0.1-SNAPSHOT.jar /app/app.jar
COPY config/dev.properties /app/dev.properties

CMD ["java", "-jar", "app.jar", "--spring.config.location=file:dev.properties"]
