package org.alliancegenome.mati.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import org.alliancegenome.mati.configuration.PostgresResource;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@QuarkusTestResource(PostgresResource.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IdentifierResourceITCase {
    private String authorization;

    @BeforeAll
    void setup() {
        authorization = "Bearer: " + OktaHelper.fetchOktaToken();
    }

    @Test
    void mintIdentifierDiseaseAnnotation() {
        checkIdentifier(mintIdentifier("disease_annotation"));
    }

    @Test
    void mintIdentifierPerson() {
        checkIdentifier(mintIdentifier("person"));
    }

    @Test
    void mintIdentifierReference() {
        checkIdentifier(mintIdentifier("reference"));
    }

    @Test
    void mintIdentifierResource() {
        checkIdentifier(mintIdentifier("resource"));
    }

    @Test
    void mintIdentifierRangeDisAnnot() {
        IdentifiersRange range = mintIdentifierRange("disease_annotation", 4);
        checkRange(range, 4);
    }

    @Test
    void mintIdentifierRangePerson() {
        IdentifiersRange range = mintIdentifierRange("person", 3);
        checkRange(range, 3);
    }

    @Test
    void mintIdentifierRangeReference() {
        IdentifiersRange range = mintIdentifierRange("reference", 2);
        checkRange(range, 2);
    }

    @Test
    void mintIdentifierRangeResource() {
        IdentifiersRange range = mintIdentifierRange("resource", 5);
        checkRange(range, 5);
    }

    private void checkIdentifier(String identifier) {
        Assertions.assertTrue(identifier.startsWith("AGRKB:"));
        Assertions.assertEquals(21, identifier.length());
    }

    private void checkRange(IdentifiersRange range, int howMany) {
        Assertions.assertTrue(range.getFirst().getCounter() > 0L);
        Assertions.assertEquals(range.getLast().getCounter() - range.getFirst().getCounter() + 1, howMany);
    }

    private String mintIdentifier(String subdomain) {
        return given().
            contentType(ContentType.JSON).
            header("Accept", "application/json").
            header("Authorization", authorization).
            header("subdomain", subdomain).
            when().
            put("http://localhost:8081/api/identifier").
            then().
            statusCode(200).
            extract().path("curie").toString();
    }

    private IdentifiersRange mintIdentifierRange(String subdomain, int howMany) {
        return given().
            contentType(ContentType.JSON).
            header("Accept", "application/json").
            header("Authorization", authorization).
            header("subdomain", subdomain).
            header("value", String.valueOf(howMany)).
            when().
            post("http://localhost:8081/api/identifier").
            then().
            extract().body().as(IdentifiersRange.class);
    }
}