#!/bin/bash

source docker/scripts/utils.sh

#docker 1.11.2
#docker-machine 0.7.0
#docker-compose 1.7.1
#Kitematic 0.10.2
#Boot2Docker ISO 1.11.2
#VirtualBox 5.0.20

DOCKER_TOOLBOX_VERSION="1.12"
DOCKER_VERSION="1.13"
DOCKER_MACHINE_VERSION="0.7.0"

DOCKER_MACHINE_NAME="testenv"

ETC_HOSTS=/etc/hosts

LOCALHOST="127.0.0.1"

HOSTS_SERVICES=( "redisdesa" "memcached-desa")
