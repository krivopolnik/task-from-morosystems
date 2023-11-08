# Use a base image with Maven 3.9.4 and Java 21
FROM maven:3.9.4-openjdk-21 AS build

# Copy the source code
COPY src /usr/src/app/src

# Copy the Maven configuration file
COPY pom.xml /usr/src/app

# Copy other resources like application.properties and keystore files
COPY resources /usr/src/app/src/main/resources

# Build the application
RUN mvn -f /usr/src/app/pom.xml clean package -DskipTests

# Use OpenJDK for the final image
FROM openjdk:21-alpine

# Add a volume for temporary files
VOLUME /tmp

# Make application's port available
EXPOSE 8081

# Copy over the built artifact from the maven image
COPY --from=build /usr/src/app/target/*.jar /server.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/server.jar"]