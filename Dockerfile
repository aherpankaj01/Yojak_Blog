
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY settings.xml /root/.m2/settings.xml

COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline

COPY src ./src
RUN mvn -B -ntp clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-Xmx1024m", "-jar", "app.jar"]
