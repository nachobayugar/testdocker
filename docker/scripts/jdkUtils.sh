#!/bin/bash

source "$HOME/.sdkman/bin/sdkman-init.sh"
source docker/scripts/utils.sh

#export NVM_DIR="/Users/$USER/.nvm"
#[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"  # This loads nvm

#### JDK ####

#function setJdk() {
#  if [ $# -ne 0 ]; then
#    removeFromPath '/System/Library/Frameworks/JavaVM.framework/Home/bin'
#    if [ -n "${JAVA_HOME+x}" ]; then
#      removeFromPath $JAVA_HOME
#    fi
#    export JAVA_HOME=`/usr/libexec/java_home -v $@`
#    export PATH=$JAVA_HOME/bin:$PATH
#  fi
#}

### Config Grails ###

function configGrailsHeap() {
  export GRAILS_OPTS="-Xmx$1G -Xms$2G -XX:MaxPermSize=$3G"
  export JAVA_OPTS="-Xmx$1G -Xms$2G -XX:MaxPermSize=$3G"
  e_note "Grails HEAP configured to $1G/$3G"
  e_note "JAVA_OPTS configured to $1G/$3G"
}

#function setGrails() {
#  version=`grep app.grails.version application.properties | sed -n '/[a-z.]*=/s///p'`
#  if [[ $version == 2* ]]; then
#    jdkversion=1.7
#  else
#    jdkversion=1.6
#  fi
#
#  sdk use grails $version > /dev/null > /dev/null
#  e_success "Grails $version configured"
#
#  setJdk $jdkversion > /dev/null > /dev/null
#  e_success "Java $jdkversion configured"
#
#}
