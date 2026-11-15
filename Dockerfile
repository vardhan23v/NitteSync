# syntax=docker/dockerfile:1
# -----------------------------------------------------------------------------
# Multi-stage Docker build for NitteSync
# Stage 1: Build the WAR artifact using Maven and Eclipse Temurin JDK 21
# Stage 2: Run in Apache Tomcat 10.1 on Eclipse Temurin JDK 21
# -----------------------------------------------------------------------------

FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compile and package application WAR
COPY src ./src
RUN mvn clean package -DskipTests

# Production runtime stage
FROM tomcat:10.1-jdk21-temurin-jammy
LABEL maintainer="Vallur Sree Vardhan <23vvardhan@gmail.com>"
LABEL description="NitteSync campus event management platform"

# Clean default Tomcat web applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Deploy NitteSync as /nittesync
COPY --from=builder /workspace/target/nittesync.war /usr/local/tomcat/webapps/nittesync.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
