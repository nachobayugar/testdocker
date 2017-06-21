#!/bin/bash

source docker/scripts/utils.sh
source docker/scripts/dockerUtils.sh
source docker/scripts/environment.sh
source docker/scripts/jdkUtils.sh
source docker/scripts/appsUtils.sh

grailsPath=$PWD

##===>CHEQUEOS

##Iniciamos los chequeos previos a la ejecución

e_header "STEP 1 - Checking installation of necessary applications"

APPS=( "brew" "brew-bundle")

runSetupChecks $APPS

checkApplicationStatus "Docker"

e_header "STEP 2 - Running Docker Compose"
docker_logo
####Docker Compose Startup
dockerComposeUp

e_header "STEP 3 - Showing application logs"

docker logs --follow webserver

e_header "STEP 4 - Stopping docker & containers"

cd ../docker/

docker-compose stop
