#!/bin/bash

if [ -z "$1" ]
then
  echo -e "\nOne argument is required. Pass the name of a docker container as an argument.\n";
  exit 1;
fi

docker logs $(docker container ls -aqf "name=$1");