# Build Stage
FROM maven:3-openjdk-17 AS build
WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

# Run Stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Adjust for JAR file
COPY --from=build /app/target/DrComputer-0.0.1-SNAPSHOT.jar drcomputer.jar
EXPOSE 8080

# Update the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "drcomputer.jar"]
