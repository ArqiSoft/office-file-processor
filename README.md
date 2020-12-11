# Office File Processor

[![Build Status](https://travis-ci.com/ArqiSoft/office-file-processor-service.svg?branch=master)](https://travis-ci.com/ArqiSoft/office-file-processor-service)

## System Requirements

Java 1.8, Maven 3.x
Optional: docker, docker-compose

## Local Build Setup

```bash
# build
mvn clean package

# run as standalone application
mvn spring-boot:run
```

## Create and start docker image

1. Use `docker-compose build` command to build the docker image.
2. Use `docker-compose up -d` command to launch the docker image.
