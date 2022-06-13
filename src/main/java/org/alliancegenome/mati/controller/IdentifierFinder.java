package org.alliancegenome.mati.controller;

import lombok.AllArgsConstructor;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.repository.SubdomainRepository;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Path("/finder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Finder", description = "Finder Operations")
public class IdentifierFinder {

    @Inject
    SubdomainSequenceRepository subdomainSequenceRepository;
    @Inject
    SubdomainRepository subdomainRepository;

    @GET
    public Response find(@HeaderParam("Authorization") final String auth_header, @HeaderParam("identifier") String identifier) {
        String trimmedID = identifier.trim();
        if (!trimmedID.startsWith("AGRKB:") || trimmedID.length() != 21)
            return Response.status(Response.Status.BAD_REQUEST).build();
        String subdomainCode = trimmedID.substring(6,9);
        String counter = trimmedID.substring(9);
        SubdomainEntity subdomainEntity = subdomainRepository.findByCode(subdomainCode);
        if (subdomainEntity == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        try {
            Long asNumber = Long.parseLong(counter);
            String status;
            if(subdomainSequenceRepository.getValue(subdomainEntity) > asNumber) {
                status = "active";
            }
            else {
                status = "inactive";
            }
            Map<String, String> map = new HashMap<>();
            map.put("status", status);
            return Response.ok().entity(map).build();
        } catch (NumberFormatException numberFormatException) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
