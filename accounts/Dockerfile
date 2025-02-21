# Build stage
FROM maven:3.8.3-openjdk-17 as build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src /app/src
RUN mvn clean package -DskipTests=true

# Run stage (final image)
FROM eclipse-temurin:17-jre-alpine-3.21

# Set timezone
RUN ln -sf /usr/share/zoneinfo/Asia/Ho_Chi_Minh /etc/localtime

# Copy only the jar from the build stage to the final image
COPY --from=build /app/target/accounts*.jar /run/app.jar

# Expose the application port
EXPOSE 8888

# Set the memory options for Java
ENV JAVA_OPTIONS="-Xmx2048m -Xms256m"

# Start the application
ENTRYPOINT ["java", "-jar", "$JAVA_OPTIONS", "/run/app.jar"]
