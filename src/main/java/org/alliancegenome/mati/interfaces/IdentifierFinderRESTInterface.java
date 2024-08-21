package org.alliancegenome.mati.interfaces;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** Returns information about an AGR identifier (e.g. AGRKB:1010000000000001) */
@Path("/finder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Finder", description = "Finder Operations")
public interface IdentifierFinderRESTInterface {

    /** Endpoint for GET
     * @param auth_header authorization token
     * @param identifier AGR identifier to analyze
     * @return an HTTP response with the status of the identifier*/
    @GET
    Response find(@NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization") final String auth_header,
                  @NotNull(message = "Header does not have identifier") @HeaderParam("identifier") String identifier);
}
