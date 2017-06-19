#!/bin/bash

source docker/scripts/utils.sh
source docker/scripts/environment.sh

function uninstall_docker() {

  e_header "Uninstalling Docker"

  e_warning "Login as root"
  sudo echo > /dev/null > /dev/null

  if [ $? -ne 0 ]; then
    e_error "Did not log as root, the installer will now exit"
    exit 1
  fi

  which docker-machine > /dev/null

  if [ $? -ne 0 ]; then
    delete_docker_machines
    e_success "Uninstalled Docker"
  fi

  e_note "Removing Applications..."
  sudo rm -rf /Applications/Docker

  e_note "Removing .docker..."
  if [[ $(echo $HOME | grep -e "^/Users.*$") == "" ]]; then
    export BASE=$HOME
  else
    export BASE="/Users"
  fi
  sudo rm -rf /$BASE/.docker

  e_note "Removing docker binaries..."
  sudo rm -f /usr/local/bin/docker
  sudo rm -f /usr/local/bin/docker-machine
  sudo rm -r /usr/local/bin/docker-machine-driver*
  sudo rm -f /usr/local/bin/docker-compose

  e_note "Removing boot2docker.iso"
  sudo rm -rf /usr/local/share/boot2docker

  e_note "Forget packages"
  sudo pkgutil --forget io.docker.pkg.docker
  sudo pkgutil --forget io.docker.pkg.dockercompose
  sudo pkgutil --forget io.docker.pkg.dockermachine
  sudo pkgutil --forget io.boot2dockeriso.pkg.boot2dockeriso

}

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
  e_note "Starting Docker compose. Please wait"
  e_note "If not found the necessary images to run the docker-compose process, we will download"
  e_note "This may take a while,be patient and go get a coffee :P"
  docker-compose up --force-recreate -d > /dev/null > /dev/null

  if [[ $? -ne 0 ]]; then
    e_error "Error in docker compose"
  else
    e_success "Docker Compose started"
  fi
}
