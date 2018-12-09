FROM maven:3.5.4-jdk-10-slim AS builder
COPY . /usr/src/office-file-processor
WORKDIR /usr/src/office-file-processor
RUN mvn -B -Pdocker package

# FROM openjdk:10.0.2-jre-slim-sid
FROM openjdk:8-jre-alpine
VOLUME /logs
RUN mkdir /temp
ENV SPRING_BOOT_APP office-file-processor.jar
ENV SPRING_BOOT_APP_JAVA_OPTS -Xmx256m -XX:NativeMemoryTracking=summary
WORKDIR /app
COPY --from=builder /usr/src/office-file-processor/target/$SPRING_BOOT_APP ./
ENTRYPOINT java $SPRING_BOOT_APP_JAVA_OPTS -jar $SPRING_BOOT_APP

