package org.alliancegenome.mati.controller;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.restassured.http.ContentType;
import org.alliancegenome.mati.configuration.PostgresResource;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static io.restassured.RestAssured.given;


@QuarkusTestResource(PostgresResource.class)
@TestHTTPEndpoint(IdentifierResource.class)
class IdentifierResourceITCase {
    private final String client_id = System.getenv("OKTA_CLIENT_ID");
    private final String client_secret= System.getenv("OKTA_CLIENT_SECRET");
    private final String okta_url = System.getenv("OKTA_URL");
    private final String okta_scopes = System.getenv("OKTA_SCOPES");

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
        String token = fetchOktaToken(client_id, client_secret, okta_scopes, okta_url);
        String authorization = "Bearer: " + token;
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
        String token = fetchOktaToken(client_id, client_secret, okta_scopes, okta_url);
        String authorization = "Bearer: " + token;
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

    private String fetchOktaToken(String client_id, String client_secret, String okta_scopes, String okta_url) {
        String authorization = "Basic " +
                Base64.getEncoder().encodeToString((client_id+":"+client_secret).getBytes());
        return given().
                contentType(ContentType.URLENC).
                header("Accept", "application/json").
                header("Cache-Control", "no-cache").
                header("Authorization", authorization).
                formParam("grant_type", "client_credentials").
                formParam("scope", okta_scopes).
                when().
                post(okta_url + "/oauth2/default/v1/token").
                then().
                extract().path("access_token").toString();
    }
}