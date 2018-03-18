#!/usr/bin/env bash

set -eu

java -jar build/libs/spring-boot-keycloak-web-0.1.0.jar "$@"
