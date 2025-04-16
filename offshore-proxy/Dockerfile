FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/offshore-proxy-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9010
ENTRYPOINT ["java", "-jar", "app.jar"]
