#!/bin/bash

cd ../../

# Build all services
cd eureka && ./mvnw clean package -DSkipTests && cd ..
cd gateway && ./mvnw clean package -DSkipTests && cd ..
cd configserver && ./mvnw clean package -DSkipTests && cd ..
cd order && ./mvnw clean package -DSkipTests && cd ..
cd user && ./mvnw clean package -DSkipTests && cd ..
cd product && ./mvnw clean package -DSkipTests && cd ..