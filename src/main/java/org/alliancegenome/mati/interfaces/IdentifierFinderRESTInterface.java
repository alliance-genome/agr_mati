package org.alliancegenome.mati.interfaces;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/finder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Finder", description = "Finder Operations")
public interface IdentifierFinderRESTInterface {

    @GET
    public Response find( @NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization") final String auth_header,
                          @NotNull(message = "Header does not have identifier") @HeaderParam("identifier") String identifier);
}
