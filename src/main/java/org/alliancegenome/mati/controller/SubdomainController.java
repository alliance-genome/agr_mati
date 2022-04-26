package org.alliancegenome.mati.controller;

import io.quarkus.security.Authenticated;
import lombok.AllArgsConstructor;
import org.alliancegenome.mati.service.SubdomainService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@AllArgsConstructor
@Path("/subdomain")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Subdomain", description = "subdomain Operations")
public class SubdomainController {
    private final SubdomainService subdomainService;

    @Authenticated
    @GET
    public Response get(@HeaderParam("Authorization") final String auth_header) {
        return Response.ok(subdomainService.findAll()).build();
    }
}
