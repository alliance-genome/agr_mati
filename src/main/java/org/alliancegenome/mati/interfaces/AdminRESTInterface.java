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
@Authenticated
public interface AdminRESTInterface {

    /**
     * Gets the counters for all the subdomains
     *
     * @param auth_header Authorization Header
     * @return an HTTP response
     */
    @Operation(summary = "Get Counter Values")
    @Path("/counters")
    @GET
    Response getCounters(@NotNull @HeaderParam("Authorization") String auth_header);


    /**
     * Sets the counters for a subdomain
     *
     * @param auth_header Authorization Header
     * @param subdomain the subdomain to change
     * @param value the value to assign
     * @return an HTTP response
     */
    @Operation(summary = "Set Counter Value")
    @Path("/counter")
    @PUT
    Response setCounter(
        @NotNull(message = "Header does not have Authorization") @HeaderParam("Authorization")
        String auth_header,

        @NotNull(message = "Header does not have subdomain") @HeaderParam("subdomain")
        String subdomain,

        @NotNull(message = "Header does not have increment value") @HeaderParam("value")
        int value
    );
}
