package org.alliancegenome.mati;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.security.Authenticated;
import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Principal;
import java.util.*;


@Path("/api")
public class TokenResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    CurrentVertxRequest request;

    @ConfigProperty(name = "okta.client.id")
    Instance<String> okta_client_id;

    @ConfigProperty(name = "okta.client.secret")
    Instance<String> okta_client_secret;

    @ConfigProperty(name = "okta.scopes")
    Instance<String> okta_scopes;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    Instance<String> okta_issuer;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/token")
    public Response token() throws IOException, InterruptedException {
        Map<String, String> result = new HashMap<>();
        String auth_header = "";
        String accessToken = "";
        String oktaTokenEndpoint = okta_issuer.get() + "/v1/token";

        auth_header = request.getCurrent().request().getHeader("Authorization");
        if(auth_header == null){
            return Response.status(400, "No Authorization Header provided").build();
        }
        HttpClient client = HttpClient.newBuilder().build();
        String requestBody = "grant_type=client_credentials" + "&scope=" + okta_scopes.get();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create(oktaTokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .header("Cache-control", "no-cache")
                .header("Authorization", auth_header)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                accessToken = mapper.readTree(response.body()).get("access_token").textValue();
                result.put("access_token", accessToken);
                return Response.ok().entity(result).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        result.put("error","Authorization Header rejected");
        return Response.serverError().entity(result).build();
    }

    @GET
    @Authenticated
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello(@Context SecurityContext ctx) {

        String name;
        Principal principal = ctx.getUserPrincipal();
        if (ctx.getUserPrincipal() == null) {
            name = "anonymous";
        } else if (!ctx.getUserPrincipal().getName().equals(jwt.getName())) {
            throw new InternalServerErrorException("Principal and JsonWebToken names do not match");
        } else {
            name = principal.getName();
        }
        return name;
    }
}
