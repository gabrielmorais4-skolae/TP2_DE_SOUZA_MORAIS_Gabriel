# ── Stage 1: build ────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /build

COPY pom.xml .
# Download project deps + download PostgreSQL driver for the WildFly module
RUN mvn dependency:go-offline -q && \
    mvn dependency:copy \
        -Dartifact=org.postgresql:postgresql:42.7.1:jar \
        -DoutputDirectory=/build/driver -q

COPY src ./src
RUN mvn package -DskipTests -q

FROM quay.io/wildfly/wildfly:latest

RUN bash -c 'mkdir -p $JBOSS_HOME/modules/org/postgresql/main'

COPY --from=build /build/driver/postgresql-42.7.1.jar \
     /tmp/postgresql-42.7.1.jar

COPY wildfly-module.xml /tmp/module.xml

RUN bash -c 'cp /tmp/postgresql-42.7.1.jar $JBOSS_HOME/modules/org/postgresql/main/ && \
             cp /tmp/module.xml           $JBOSS_HOME/modules/org/postgresql/main/module.xml'

COPY wildfly-postgresql-setup.cli /tmp/wildfly-setup.cli
RUN bash -c '$JBOSS_HOME/bin/jboss-cli.sh --file=/tmp/wildfly-setup.cli'

COPY --from=build /build/target/products-api-1.0.0.war \
     /tmp/products-api-1.0.0.war
RUN bash -c 'cp /tmp/products-api-1.0.0.war $JBOSS_HOME/standalone/deployments/'

EXPOSE 8080 9990

CMD ["/bin/bash", "-c", "$JBOSS_HOME/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0"]
