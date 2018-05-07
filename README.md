# Photobooth

A web photobooth in [vertx](https://vertx.io/)

## Introduction

It's a simple webphotobooth in vertx 

There are some verticles for :

* serve the web page and the socket.io binding to the vertx eventBus
* a picture verticle that take high res photo (with the linux command streamer) and compose it
* a printer verticle that print the photo
* (TODO) a binding verticle to lirc 

## Run

``mvn clean package && java -jar target/photobooth-1.0.0-SNAPSHOT-fat.jar -conf ./config.json ``

## Config

There is a config file ``config.json`` with some parameters