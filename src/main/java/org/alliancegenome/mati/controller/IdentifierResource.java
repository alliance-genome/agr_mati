package org.alliancegenome.mati.controller;

import io.quarkus.security.Authenticated;
import lombok.AllArgsConstructor;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Path("/identifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Identifier", description = "identifier Operations")
public class IdentifierResource {

    @Inject
    SubdomainSequenceRepository subdomainSequenceRepository;

    private Response makeResultResponse(Long result) {
        if(result == -1L) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            Map<String, Long> map = new HashMap<>();
            map.put("value", result);
            return Response.ok().entity(map).build();
        }
    }

    @Authenticated
    @GET
    public Response get(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain) {
        Long result = subdomainSequenceRepository.getValue(subdomain);
        return makeResultResponse(result);
    }

    @Authenticated
    @POST
    public Response increment(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain) {
        Long result = subdomainSequenceRepository.increment(subdomain);
        return makeResultResponse(result);
    }

    @Authenticated
    @PUT
    public Response increment(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain, @HeaderParam("value") int value) {
        Long result = subdomainSequenceRepository.increment(subdomain, value);
        return makeResultResponse(result);
    }
}
