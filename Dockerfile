# Build Stage
FROM maven:3.8.5-openjdk-17 AS build

COPY . .
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17.0.1-jdk-slim

# Adjust for JAR file
COPY --from=build /target/user-management-0.0.1-SNAPSHOT.jar demo.jar
EXPOSE 8080

# Update the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "demo.jar"]
