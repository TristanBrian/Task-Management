FROM eclipse-temurin:11-jre-alpine
COPY target/task-management-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]