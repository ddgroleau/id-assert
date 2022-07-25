#!/bin/bash

export SPRING_ACTIVE_PROFILES=dev
mvn install -DskipTests
docker build -t dangroleau/id-assert .
#docker push dangroleau/id-assert
docker-compose up -d