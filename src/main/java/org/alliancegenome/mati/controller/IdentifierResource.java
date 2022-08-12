package org.alliancegenome.mati.controller;

import io.quarkus.security.Authenticated;
import lombok.AllArgsConstructor;
import org.alliancegenome.mati.configuration.ErrorResponse;
import org.alliancegenome.mati.entity.Identifier;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.repository.SubdomainRepository;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
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

    private String formatCounter(Long counter, SubdomainEntity subdomainEntity) {
        StringBuffer identifier = new StringBuffer("AGRKB:");
        identifier.append(subdomainEntity.getCode());
        identifier.append(String.format("%0" + 12 + "d", counter));
        return identifier.toString();
    }

    private Response makeResultResponse(SubdomainEntity subdomainEntity, Long counter) {
        if (counter == -1L) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier","Failure retrieving/incrementing the counter");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("value", formatCounter(counter, subdomainEntity));
            return Response.ok().entity(map).build();
        }
    }

    private Response makeResultResponse(SubdomainEntity subdomainEntity, Long firstValue, Long lastValue) {
        if ( firstValue==-1L || lastValue==-1L) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier","Failure incrementing the counter");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        } else {
            Identifier first = new Identifier(firstValue, subdomainEntity.getCode(), subdomainEntity.getName(),
                formatCounter(firstValue, subdomainEntity));
            Identifier last = new Identifier(lastValue, subdomainEntity.getCode(), subdomainEntity.getName(),
                formatCounter(lastValue, subdomainEntity));
            IdentifiersRange identifiersRange = new IdentifiersRange(first, last);
            return Response.ok().entity(identifiersRange).build();
        }
    }

    @Authenticated
    @GET
    public Response get(@NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization") final String auth_header,
                        @NotNull(message = "Header does not have subdomain") @HeaderParam("subdomain") String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","ID subdomain " + subdomainEntity.getCode() +" not found");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        Long result = subdomainSequenceRepository.getValue(subdomainEntity);
        return makeResultResponse(subdomainEntity, result);
    }

    @Authenticated
    @PUT
    public Response increment(@NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization") final String auth_header,
                              @NotNull(message = "Header does not have subdomain") @HeaderParam("subdomain") String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","ID subdomain " + subdomainEntity.getCode() +" not found");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        Long result = subdomainSequenceRepository.increment(subdomainEntity);
        return makeResultResponse(subdomainEntity, result);
    }

    @Authenticated
    @POST
    @Transactional
    public Response increment(@NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization") final String auth_header,
                              @NotNull(message = "Header does not have subdomain") @HeaderParam("subdomain") String subdomain,
                              @NotNull(message = "Header does not have increment value") @HeaderParam("value") int value) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier.post","ID subdomain " + subdomainEntity.getCode() +" not found");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        Long firstValue = subdomainSequenceRepository.getValue(subdomainEntity) + 1;
        Long lastValue = subdomainSequenceRepository.increment(subdomainEntity, value);
        if (firstValue == -1L || lastValue == -1L) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier.post","failure incrementing subdomain " + subdomainEntity.getCode());
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();

        }
        return makeResultResponse(subdomainEntity, firstValue, lastValue);
    }
}
