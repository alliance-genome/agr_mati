package org.alliancegenome.mati.controller;

import io.quarkus.security.Authenticated;
import lombok.AllArgsConstructor;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.repository.SubdomainRepository;
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
    @Inject
    SubdomainRepository subdomainRepository;


    private Response makeResultResponse(SubdomainEntity subdomainEntity, Long counter) {
        if (counter == -1L) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            Map<String, String> map = new HashMap<>();
            StringBuffer identifier = new StringBuffer("AGRKB:");
            identifier.append(subdomainEntity.getCode());
            identifier.append(String.format("%0" + 12 + "d", counter));
            map.put("value", identifier.toString());
            return Response.ok().entity(map).build();
        }
    }

    @Authenticated
    @GET
    public Response get(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Long result = subdomainSequenceRepository.getValue(subdomainEntity);
        return makeResultResponse(subdomainEntity, result);
    }

    @Authenticated
    @PUT
    public Response increment(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Long result = subdomainSequenceRepository.increment(subdomainEntity);
        return makeResultResponse(subdomainEntity, result);
    }

    @Authenticated
    @POST
    public Response increment(@HeaderParam("Authorization") final String auth_header, @HeaderParam("subdomain") String subdomain, @HeaderParam("value") int value) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        Long result = subdomainSequenceRepository.increment(subdomainEntity, value);
        return makeResultResponse(subdomainEntity, result);
    }
}
