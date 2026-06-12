# Spring Boot — single stage
# JAR is already built by Maven before Docker build
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR built by Maven
COPY target/*.jar app.jar

# JVM memory optimization for small EC2 instance
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8081

CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]