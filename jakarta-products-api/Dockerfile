# Stage 1: build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -q && \
    mvn dependency:copy \
        -Dartifact=fish.payara.extras:payara-micro:6.2024.6:jar \
        -DoutputDirectory=/build/payara -q

COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2: run
FROM eclipse-temurin:17-jre

WORKDIR /opt/app

COPY --from=build /build/target/products-api-1.0.0.war app.war
COPY --from=build /build/payara/payara-micro-6.2024.6.jar payara-micro.jar
COPY docker/postboot.asadmin postboot.asadmin

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/opt/app/payara-micro.jar", \
            "--deploy", "/opt/app/app.war", \
            "--postbootcommandfile", "/opt/app/postboot.asadmin", \
            "--autoBindHttp"]
