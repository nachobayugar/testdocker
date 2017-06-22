#!/bin/bash

#Script encargado de instalar todo lo necesario para correr los tests.

source docker/scripts/utils.sh

function removeFromPath() {
  export PATH=$(echo $PATH | sed -E -e "s;:$1;;" -e "s;$1:?;;")
}

function installHomebrew() {
  # Check if Homebrew is installed
  which -s brew
  if [[ $? != 0 ]] ; then
      # Install Homebrew
      e_note "Installing Brew"
      /usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
  else
      e_success "Brew installed"
      BREW_VERSION=$(brew --version)
      e_success "Brew version: $BREW_VERSION"
  fi
}


function buildBrewBundle() {
  e_note "Building Brew Bundle"
  brew bundle
}

function checkBrewBundle() {
    cd docker/scripts/
    CHECK=`brew bundle check` > /dev/null
    if [[ $CHECK = "The Brewfile's dependencies are satisfied." ]]; then
        e_success "Brew Bundle Installed"
    else
        e_error "Brew Bundle Not installed"
        buildBrewBundle
    fi
}

function installBrewApplication() {
    e_note "Installing $1"
    brew install $1
    checkBrewAppSetup $1
}

function installJavaWithSdkman() {
  e_note "Installing Java v$1"
  echo Y | sdk install java $1 > /dev/null
}

function installGrailsWithSdkman() {
  e_note "Installing Grails v$1"
  echo Y | sdk install grails $1 > /dev/null
}

function installBrewCaskApplication() {
  echo "------------------------------------------------"
  echo "Installing $1"
  echo "------------------------------------------------"

  brew cask install $1
}

function install_cask() {
  brew tap caskroom/cask
}


function checkSdkman() {

  sdkman_version=$SDKMAN_VERSION

  if [[ $SDKMAN_VERSION == "" ]] ; then
    e_error "SDKMAN Not installed"
    e_note "Installing SDKMAN"
    curl -s "https://get.sdkman.io" | bash
    source "$HOME/.sdkman/bin/sdkman-init.sh"
  else
    sdkman_version=$SDKMAN_VERSION
    e_success "SDKMAN Installed [$sdkman_version]"
    source "$HOME/.sdkman/bin/sdkman-init.sh"
  fi

}

function checkGrailsVersion() {

  source "$HOME/.sdkman/bin/sdkman-init.sh"

  cd $grailsPath
  GRAILS_VERSION=`grep app.grails.version application.properties | sed -n '/[a-z.]*=/s///p'`


  if [[ $GRAILS_VERSION == 1.3.7 ]]; then
    GRAILS_FOLDER="$HOME/.sdkman/candidates/grails/$GRAILS_VERSION"
    if [ -d "$GRAILS_FOLDER" ]; then
        e_success "Grails v$GRAILS_VERSION Installed"
        sdk use grails $GRAILS_VERSION > /dev/null
        if [[ $? == 0 ]]; then
            e_success "Grails v$GRAILS_VERSION setted"
        else
            e_error "Error Setting Grails v$GRAILS_VERSION"
        fi
    else
        e_error "Grails v$GRAILS_VERSION NOT installed"
        sdk install grails $GRAILS_VERSION > /dev/null
    fi
  else
    GRAILS_STATUS=`sdk list grails | sed 's/*/w/g' | egrep 'w [0-9.]+' -o | grep "$GRAILS_VERSION"`
    if [[ $GRAILS_STATUS != "" ]]; then

        e_success "Grails v$GRAILS_VERSION installed"
        sdk use grails $GRAILS_VERSION > /dev/null
        if [[ $? == 0 ]]; then
            e_success "Grails v$GRAILS_VERSION setted"
        else
            e_error "Error Setting Grails v$GRAILS_VERSION"
        fi
    else
        e_error "Grails v$GRAILS_VERSION NOT installed"
        installGrailsWithSdkman $GRAILS_VERSION
    fi
  fi
}


function checkJavaVersion() {

  #Funcion para comprobar atraves de sdkman la version de java

  source "$HOME/.sdkman/bin/sdkman-init.sh"
  cd $grailsPath
  GRAILS_VERSION=`grep app.grails.version application.properties | sed -n '/[a-z.]*=/s///p'`

  if [[ $GRAILS_VERSION == 1.3.7 ]]; then
    JAVA_VERSION="6u65"
    JAVA_STATUS=`sdk list java | sed 's/*/w/g' | egrep 'w [0-9u]+' -o | grep "$JAVA_VERSION"`
  else
    JAVA_VERSION="7u79"
    JAVA_STATUS=`sdk list java | sed 's/*/w/g' | egrep 'w [0-9u]+' -o | grep "$JAVA_VERSION"`
  fi

  if [[ $JAVA_STATUS != "" ]]; then
    e_success "JAVA v$JAVA_VERSION installed"
    sdk use java $JAVA_VERSION > /dev/null
    if [[ $? == 0 ]]; then
        e_success "Java v$JAVA_VERSION setted"
        #sdk default java $JAVA_VERSION > /dev/null
    else
        e_error "Error Setting Java v$JAVA_VERSION"
    fi
  else
    e_error "JAVA v$JAVA_VERSION Not installed"
    installJavaWithSdkman $JAVA_VERSION
  fi
}

function runSetupCheck() {
  #Funcion para saber si una app esta instalada
  #@Params: Aplicacion a comprobar
  #@Response: Si esta instalado devuelve la ruta si no vacio.
  APP=$1
  case $APP in
    "sdkman")
        checkSdkman
    ;;
    "brew-bundle")
        checkBrewBundle
    ;;
    "java")
        checkJavaVersion
    ;;
    "grails")
        checkGrailsVersion
    ;;
    "brew")
        installHomebrew
    ;;
    *)
        which $APP > /dev/null

        if [[ $? != "" ]]; then
            e_success "$APP installed"
        else
            e_error "$APP Not installed"
        fi
    ;;
  esac

}

function checkBrewAppSetup() {
  BREW_APP=`brew ls --versions | grep -i $1  | awk {'print $1'}`
  BREW_APP_VERSION=`brew ls --versions | grep -i $1  | awk {'print $2'}`
  if [[ $? = "$1" ]]; then
    e_success "$BREW_APP installed"
  else
    e_error "$BREW_APP Not installed"

  fi
}

function checkApplicationStatus() {
  #Funcion para saber si una aplicacion esta ejecutandose
  #@Params: Aplicacion a comprobar
  #@Response: pid(Process ID)
  APP="$1"
  #PID=$(ps ax | grep -v grep | grep $APP | awk {'print $1'})
  PID=`pgrep -x "$APP"`

  if [[ $PID != "" ]]; then
    e_success "$APP is Running - PID:[$PID]"
  else
    e_error "$APP isn't Running"
    e_note "Running $APP"
    openApplication "$APP"
  fi

}

function openApplication() {
  #Funcion para abrir una aplicacion en MacOS
  #@Params: Aplicacion a abrir
  #@Response: pid(Process ID)
  APPLICATION="$1"
  open /Applications/$1.app > /dev/null > /dev/null
  sleep 10
  checkApplicationStatus $APP
}

function runSetupChecks() {
  #Funcion para abrir una aplicacion en MacOS
  #@Params: Aplicacion a abrir
  #@Response: pid(Process ID)
  APPS=$1
  for i in "${APPS[@]}"
  do
    runSetupCheck "$i"
  done
}
