package org.alliancegenome.mati.controller;

import io.restassured.http.ContentType;

import java.util.Base64;

import static io.restassured.RestAssured.given;

public class OktaHelper {
    private static final String client_id = System.getenv("OKTA_CLIENT_ID");
    private static final String client_secret= System.getenv("OKTA_CLIENT_SECRET");
    private static final String okta_url = System.getenv("OKTA_URL");
    private static final String okta_scopes = System.getenv("OKTA_SCOPES");

    public static String fetchOktaToken() {
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
