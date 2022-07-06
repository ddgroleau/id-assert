#!/bin/bash

mvn install -DskipTests
sudo service postgresql start

export SPRING_ACTIVE_PROFILES=dev
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/id_assert
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export CLIENT_ID=id-assert
export CLIENT_SECRET=secret
export REDIRECT_URI=http://127.0.0.1:8080
export ISSUER_URI=http://127.0.0.1:8080

mvn spring-boot:run