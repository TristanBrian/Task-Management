FROM openjdk:11-jre-slim
COPY target/task-management-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]