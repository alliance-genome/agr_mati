package org.alliancegenome.mati.interfaces;

import io.quarkus.security.Authenticated;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/** Interface for Administrative endpoints  */
@Path("/admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin", description = "Administrative tasks")
public interface AdminRESTInterface {

    /**
     * Gets the counters for all the subdomains
     *
     * @return an HTTP response
     */
    @Operation(summary = "Get Counter Values")
    @Path("/counters")
    @GET
    Response getCounters();

    /**
     * Rolls down the counters to another environment
     * prod to beta, beta to alpha
     * @param auth_header Authorization Header
     * @return an HTTP response
     */
    @Operation(summary = "Roll down")
    @Path("/rolldown_for_curation")
    @POST
    @Authenticated
    Response rolldown_for_curation(
            @NotNull(message = "Header does not have Authorization")  @HeaderParam("Authorization") String auth_header);
}
