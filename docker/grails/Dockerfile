#Base image for building grails application
FROM hub.furycloud.io/mercadolibre/melibuntujdk:7

LABEL Description="This image is used to run a grails application (v2.4.5) with JDK v1.7 " Version="1.0"

ENV GRAILS_VERSION 2.4.5

#Install grails
RUN mkdir /apps \
  & mkdir /webserver
RUN wget "https://github.com/grails/grails-core/releases/download/v$GRAILS_VERSION/grails-$GRAILS_VERSION.zip" -O /tmp/grails.zip && unzip /tmp/grails.zip -d /apps && mv /apps/grails-* /apps/grails
ENV GRAILS_HOME /apps/grails
ENV PATH $PATH:$GRAILS_HOME/bin

RUN apt-get update
