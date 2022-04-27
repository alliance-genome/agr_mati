package org.alliancegenome.mati.controller;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
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
        given()
            .when()
            .get("/api/subdomain/1")
            .then()
            .statusCode(200);
    }
}