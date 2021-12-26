FROM maven:3.8.3-openjdk-11-slim AS MAVEN_BUILD
MAINTAINER Toby Peel
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn clean package

FROM openjdk:11-jdk-slim
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/employee-service-1.0.0.jar /app/
ENTRYPOINT ["java","-jar","employee-service-1.0.0.jar"]
