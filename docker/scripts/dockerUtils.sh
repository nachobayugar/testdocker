#!/bin/bash

source docker/scripts/utils.sh
source docker/scripts/environment.sh

function docker_logo() {
  echo ""
  echo ""
  echo "                    ##        .            "
  echo "              ## ## ##       ==            "
  echo "           ## ## ## ##      ===            "
  echo "       /""""""""""""""""\\\___/ ===        "
  echo "  ~~~ {~~ ~~~~ ~~~ ~~~~ ~~ ~ /  ===- ~~~   "
  echo "       \\\______ o          __/            "
  echo "         \\\    \\\        __/             "
  echo "          \\\____\\\______/                "
  echo "                                           "
  echo "           |          |                    "
  echo "        __ |  __   __ | _  __   _          "
  echo "       /  \| /  \ /   |/  / _\ |           "
  echo "       \__/| \__/ \__ |\_ \__  |           "
  echo ""
  echo ""
}

  function dockerComposeUp() {
    cd $grailsPath
  CURRENT=$(pwd)
  BASENAME=$(basename "$CURRENT")

  case $BASENAME in
      testdocker)
        p="$CURRENT/docker"
      ;;
      webserver)
        p="$CURRENT/../docker"
      ;;
  esac

  e_note "Starting Docker compose. Please wait"
  e_note "If not found the necessary images to run the docker-compose process, we will download"
  e_note "This may take a while,be patient and go get a coffee :P"
  docker-compose --file "$p/docker-compose.yml" up --force-recreate -d > /dev/null > /dev/null

  if [[ $? -ne 0 ]]; then
    e_error "Error in docker compose"
  else
    e_success "Docker Compose started"
  fi
}
