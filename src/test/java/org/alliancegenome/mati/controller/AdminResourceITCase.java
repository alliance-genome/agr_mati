package org.alliancegenome.mati.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.alliancegenome.mati.configuration.PostgresResource;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;


import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminResourceITCase {
    private String authorization;
    @BeforeAll
    void setup() {
        authorization = "Bearer: " + OktaHelper.fetchOktaToken();
    }

    @Test
    public void setCounterDA() {
        setCounterandCheckStatus("disease_annotation", 2);
    }

    @Test
    public void setCounterPerson() {
        setCounterandCheckStatus("person", 4);
    }

    @Test
    public void setCounterResource() {
        setCounterandCheckStatus("resource", 8);
    }

    @Test
    public void getCounters() {
        Map<String,Integer> counters = given().
            contentType(JSON).
            header("Accept", "application/json").
            header("Authorization", authorization).
            when().
            get("http://localhost:8081/api/admin/counters").
            then().
            statusCode(200)
            .extract().body().as(Map.class);

        Assertions.assertEquals(counters.get("disease_annotation"), 2);
        Assertions.assertEquals(counters.get("person"), 4);
        Assertions.assertEquals(counters.get("resource"), 8);
    }

    private void setCounterandCheckStatus(String subdomain, int value) {
        given().
            contentType(JSON).
            header("Authorization", authorization).
            header("subdomain", subdomain).
            header("value", value).
            when().
            put("http://localhost:8081/api/admin/counter").
            then().
            statusCode(200);
    }
}
