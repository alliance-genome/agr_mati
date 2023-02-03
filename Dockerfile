### Stage 1: build API
FROM maven:3.8-eclipse-temurin-17 as BUILD_API_STAGE
ARG OVERWRITE_VERSION
WORKDIR /agr_mati

# copy the src code to the container
COPY ./ ./

# Optionally overwrite the application version stored in the pom.xml
RUN if [ "${OVERWRITE_VERSION}" != "" ]; then \
        mvn versions:set -ntp -DnewVersion=$OVERWRITE_VERSION; \
    fi;
# build the api jar
RUN mvn -T 8 clean package -Dquarkus.package.type=uber-jar -ntp

### Stage 2: build final application image
FROM eclipse-temurin:17-jre-alpine
ARG OVERWRITE_VERSION
WORKDIR /agr_mati

# copy only the artifacts we need from the first stage and discard the rest
COPY --from=BUILD_API_STAGE /agr_mati/target/agr_mati-${OVERWRITE_VERSION}-runner.jar ./agr_mati-runner.jar

# Expose necessary ports
EXPOSE 8080

# Set default env variables for local docker application execution
ENV QUARKUS_DATASOURCE_JDBC_URL jdbc:postgresql://postgres:5432/mati
ENV QUARKUS_DATASOURCE_USERNAME postgres
ENV QUARKUS_DATASOURCE_PASSWORD postgres

# Start the application
CMD ["java", "-Xmx820m", "-jar", "agr_mati-runner.jar"]
