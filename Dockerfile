FROM openjdk:21-jdk-slim
WORKDIR /app
COPY . .
ARG INTERNAL_REPO_LOGIN
ARG INTERNAL_REPO_PASSWORD
ENV INTERNAL_REPO_LOGIN=$INTERNAL_REPO_LOGIN
ENV INTERNAL_REPO_PASSWORD=$INTERNAL_REPO_PASSWORD
RUN chmod +x gradlew && ./gradlew assemble
EXPOSE 8080
CMD ["java", "-Dspring.profiles.active=prod", "-jar", "/app/build/libs/Booking-0.0.1-SNAPSHOT.jar"]
