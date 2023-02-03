package org.alliancegenome.mati.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.response.ValidatableResponse;

import org.alliancegenome.mati.configuration.PostgresResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresResource.class)
class SubdomainResourceITCase {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void getAll() {
        given()
            .when()
            .get("/api/subdomain")
            .then()
            .statusCode(200);
    }

    @Test
    public void getOne() {
        ValidatableResponse response = given()
                                       .queryParam("id", "1")
                                       .when()
                                       .get("/api/subdomain")
                                       .then();
        response.statusCode(200);

        assertThat(response.extract().jsonPath().getList("$").size(), equalTo(1));
    }
}
