FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Install common into local Maven repo
COPY common/pom.xml common/
COPY common/src common/src
RUN mvn install -f common/pom.xml -DskipTests

# Build notification-service
COPY notification-service/pom.xml notification-service/
COPY notification-service/src notification-service/src
RUN mvn clean package -f notification-service/pom.xml -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]