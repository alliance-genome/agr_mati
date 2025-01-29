package org.alliancegenome.mati.configuration;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Optional;

/** Docker container with Postgres databases to run in our integration tests */
public class PostgresResource implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    private Optional<String> matiContainerNetworkId;
    private JdbcDatabaseContainer matiContainer;
    private JdbcDatabaseContainer rolldownContainer;
    private final String DOCKER_IMAGE = "postgres:13";

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        matiContainerNetworkId = context.containerNetworkId();
    }

    @Override
    public Map<String, String> start() {
        matiContainer = new PostgreSQLContainer<>(DOCKER_IMAGE).withDatabaseName("mati").withLogConsumer(outputFrame -> {});
        matiContainerNetworkId.ifPresent(matiContainer::withNetworkMode);
        matiContainer.start();
        String matiJdbcUrl = matiContainer.getJdbcUrl();
        if (matiContainerNetworkId.isPresent()) {
            matiJdbcUrl = fixJdbcUrl(matiJdbcUrl, matiContainer);
        }

        rolldownContainer = new PostgreSQLContainer<>(DOCKER_IMAGE).withDatabaseName("roll").withLogConsumer(outputFrame -> {});
        rolldownContainer.start();
        String rolldownJdbcUrl = rolldownContainer.getJdbcUrl();

        return ImmutableMap.of(
                "quarkus.datasource.username", matiContainer.getUsername(),
                "quarkus.datasource.password", matiContainer.getPassword(),
                "quarkus.datasource.jdbc.url", matiJdbcUrl,
                "quarkus.datasource.rolldown.username", rolldownContainer.getUsername(),
                "quarkus.datasource.rolldown.password", rolldownContainer.getPassword(),
                "quarkus.datasource.rolldown.jdbc.url", rolldownJdbcUrl);
    }

    private String fixJdbcUrl(String jdbcUrl, JdbcDatabaseContainer container) {
        String hostPort = container.getHost() + ':' + container.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
        String networkHostPort =
                container.getCurrentContainerInfo().getConfig().getHostName()
                        + ':'
                        + PostgreSQLContainer.POSTGRESQL_PORT;
        return jdbcUrl.replace(hostPort, networkHostPort);
    }

    @Override
    public void stop() { }
}