package org.alliancegenome.mati.configuration;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

/** Docker container with Postgres database to run in our integration tests */
public class PostgresResource implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    private Optional<String> containerNetworkId;
    private JdbcDatabaseContainer container;

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        containerNetworkId = context.containerNetworkId();
    }

    @Override
    public Map<String, String> start() {
        container = new PostgreSQLContainer<>("postgres:13").withLogConsumer(outputFrame -> {});
        containerNetworkId.ifPresent(container::withNetworkMode);
        container.start();

        String jdbcUrl = container.getJdbcUrl();
        if (containerNetworkId.isPresent()) {
            jdbcUrl = fixJdbcUrl(jdbcUrl);
        }

        return ImmutableMap.of(
                "quarkus.datasource.username", container.getUsername(),
                "quarkus.datasource.password", container.getPassword(),
                "quarkus.datasource.jdbc.url", jdbcUrl);
    }

    private String fixJdbcUrl(String jdbcUrl) {
        String hostPort = container.getHost() + ':' + container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
        String networkHostPort =
                container.getCurrentContainerInfo().getConfig().getHostName()
                        + ':'
                        + PostgreSQLContainer.POSTGRESQL_PORT;
        return jdbcUrl.replace(hostPort, networkHostPort);
    }

    @Override
    public void stop() {
    }
}