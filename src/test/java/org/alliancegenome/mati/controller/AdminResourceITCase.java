package org.alliancegenome.mati.controller;

import io.quarkus.test.common.WithTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;
import org.alliancegenome.mati.configuration.PostgresResource;
import org.junit.jupiter.api.*;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

/**
 * Tests the /api/admin/counters endpoint
 * It runs *after* the IdentifierResourceITcase tests
 * that mint some identifiers
 */
@QuarkusIntegrationTest
@WithTestResource(value = PostgresResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Order(3)
public class AdminResourceITCase {

    private String authorization;

    @BeforeAll
    void setup() {
        authorization = "Bearer: " + OktaHelper.fetchOktaToken();
    }

    @Test
    public void getCounters() {
        Map counters = given().
            contentType(JSON).
            header("Accept", "application/json").
            when().
            get("http://localhost:8081/api/admin/counters").
            then().
            statusCode(200)
            .extract().body().as(Map.class);

        Assertions.assertEquals(counters.get("disease_annotation"), 5);
        Assertions.assertEquals(counters.get("person"), 4);
        Assertions.assertEquals(counters.get("resource"), 6);
        Assertions.assertEquals(counters.get("reference"), 3);
    }

    /**
     * The following request using a valid token is *not* authorized
     * Hypothesis:
     * In integration-test mode the authentication succeeds only
     * with the /api/identifier endpoint. However, it succeeds with all
     * endpoints in development and production modes.
     */
    @Test
    public void testCurationRolldown() throws Exception {

        given().
            contentType(ContentType.JSON).
            header("Accept", "application/json").
            header("Authorization", authorization).
            when().
            post("http://localhost:8081/api/admin/rolldown_for_curation").
            then().statusCode(401);
    }
}
