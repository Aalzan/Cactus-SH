# Use Amazon Corretto 17 as the base image
FROM amazoncorretto:17

# Set the working directory in the container
WORKDIR /app

# Copy the Gradle build files
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src/ ./src/

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew clean build -x test

# Copy the built jar file into the container
COPY build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "app.jar"]
