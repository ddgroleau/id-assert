#!/bin/bash

mvn install -DskipTests
sudo service postgresql start
export SPRING_ACTIVE_PROFILES=dev
mvn spring-boot:run