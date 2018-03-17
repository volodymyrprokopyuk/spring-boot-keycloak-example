#!/usr/bin/env bash

set -eu

java -jar build/libs/spring-boot-keycloak-example-0.1.0.jar "$@"
