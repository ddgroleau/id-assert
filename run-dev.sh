#!/bin/bash

mvn install -DskipTests
sudo service postgresql start

export SPRING_ACTIVE_PROFILES=dev
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/id_assert
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export IDENTITY_BASE_URI=http://127.0.0.1:8080
export DEFAULT_CLIENT_ID=clientId
export DEFAULT_CLIENT_NAME=clientName
export DEFAULT_CLIENT_SECRET=clientSecret

mvn spring-boot:run