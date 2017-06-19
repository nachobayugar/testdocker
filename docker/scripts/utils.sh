#!/bin/bash

#### Set Colors #####

bold=$(tput bold)
underline=$(tput sgr 0 1)
reset=$(tput sgr0)

purple=$(tput setaf 171)
red=$(tput setaf 1)
green=$(tput setaf 76)
tan=$(tput setaf 3)
blue=$(tput setaf 38)


#### Headers and  Logging #####

e_header() { printf "\n${bold}${purple}==========  %s  ==========${reset}\n\n" "$@"
}
e_arrow() { printf "➜ $@\n"
}
e_success() { printf "${green}✔ %s${reset}\n" "$@"
}
e_error() { printf "${red}✖ %s${reset}\n" "$@"
}
e_warning() { printf "${tan}➜ %s${reset}\n" "$@"
}
e_underline() { printf "${underline}${bold}%s${reset}\n" "$@"
}
e_bold() { printf "${bold}%s${reset}\n" "$@"
}
e_note() { printf "${underline}${bold}${blue}➜${reset}  ${blue}%s${reset}\n" "$@"
}

#### DOWNLOAD ####

function download() {
  URL=$1
  DEST=$2
  curl -o $DEST $URL
}

##Manage /etc/hosts

function get_ip() {
  docker-machine ip $1
}

function do_backup() {
  log_op "Starting Docker machine $1"
  log_op "Doing a Backup of your $ETC_HOSTS \n"
  sudo sed -i.backup "s/$2/$3/g" /etc/hosts
}

function doReplace() {

  #list of services to add
  #doReplace $LOCALHOST redisdesa $ETC_HOSTS
  #HOSTS_SERVICES=( "redisdesa" "memcached-desa" "mongo-desa" "elastic_search-desa")
  HOSTS_SERVICES=$2

  for i in "${HOSTS_SERVICES[@]}"
  do

    SERVICE_LINE="$1\t$i"

    if [ -n "$(grep $SERVICE_LINE /etc/hosts)" ]; then
      e_success "$SERVICE_LINE Founded in $ETC_HOSTS - It is not necessary to update the line"
    else
      e_warning "$SERVICE_LINE wasn't found in your file $ETC_HOSTS. We need to add the necessary lines"
      e_warning "To do that, You need login as root"

      sudo echo > /dev/null > /dev/null

      if [ $? -ne 0 ]; then
        error "Did not log as root, the installer will now exit"
        exit 1
      fi

      e_note "Adding $SERVICE_LINE to your $ETC_HOSTS"

      sudo -- sh -c -e "echo $'#$i' >> /etc/hosts";
      sudo -- sh -c -e "echo '$SERVICE_LINE' >> /etc/hosts";

      if [ "$(grep $SERVICE_LINE /etc/hosts)" ]; then
        e_success "$SERVICE_LINE inserted into /etc/hosts"
      else
        e_error "An error occurred during line insertion"
        exit 1
      fi
    fi
  done

}
