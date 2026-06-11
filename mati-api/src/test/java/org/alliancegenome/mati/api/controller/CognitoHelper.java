package org.alliancegenome.mati.api.controller;

import io.restassured.http.ContentType;

import java.util.Base64;

import static io.restassured.RestAssured.given;

public class CognitoHelper {
    private static final String client_id = System.getenv("COGNITO_CLIENT_ID");
    private static final String client_secret = System.getenv("COGNITO_CLIENT_SECRET");
    private static final String cognito_url = System.getenv("COGNITO_URL");
    private static final String cognito_scopes = System.getenv("COGNITO_SCOPES");

    public static String fetchCognitoToken() {
        String authorization = "Basic " +
                Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes());
        return given().
                contentType(ContentType.URLENC).
                header("Accept", "application/json").
                header("Cache-Control", "no-cache").
                header("Authorization", authorization).
                formParam("grant_type", "client_credentials").
                formParam("scope", cognito_scopes).
                when().
                post(cognito_url + "/oauth2/token").
                then().
                extract().path("access_token").toString();
    }
}
