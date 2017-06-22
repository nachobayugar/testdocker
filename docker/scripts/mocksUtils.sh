#!/bin/bash

source docker/scripts/utils.sh

function readFile() {
  while IFS='' read -r line || [[ -n "$line" ]]; do
      MOCKS_HOME=$line
  done < "$1"
}

function getMocksServerHome() {

  file="$HOME/.mocks_server_home.txt"

  if [ -e "$file" ]; then

    readFile $file

    if [ -d "$MOCKS_HOME" ]; then
      e_success "Directory $MOCKS_HOME exists"
    else
      e_error "Mocks server file found, but fraud-mocks path wasn't found. We need to add the necessary lines"
      e_warning "To do that, You need login as root"

      sudo echo > /dev/null > /dev/null

      if [ $? -ne 0 ]; then
        error "Did not log as root, the installer will now exit"
        exit 1
      fi

      e_warning "Now type your absolute mocks home path -> e.g (/Users/user_name/Workspace/my-project/mocks)"

      read MOCKS_HOME

      sudo -- sh -c -e "echo '$MOCKS_HOME' >> $file";
    fi

  else
    e_warning "Mocks Home File wasn't found in your HOME directory. We need create that file"
    e_warning "To do that, You need login as root"

    sudo echo > /dev/null > /dev/null

    if [ $? -ne 0 ]; then
      error "Did not log as root, the installer will now exit"
      exit 1
    fi

    touch $file

    e_warning "Now type your absolute mocks home path -> e.g (/Users/user_name/Workspace/my-project/mocks)"

    read MOCKS_HOME

    sudo -- sh -c -e "echo '$MOCKS_HOME' >> $file";

  fi
}
