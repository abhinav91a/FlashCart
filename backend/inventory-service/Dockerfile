FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Install common into local Maven repo
COPY common/pom.xml common/
COPY common/src common/src
RUN mvn install -f common/pom.xml -DskipTests

# Build inventory-service
COPY inventory-service/pom.xml inventory-service/
COPY inventory-service/src inventory-service/src
RUN mvn clean package -f inventory-service/pom.xml -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/inventory-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]