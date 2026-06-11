package org.alliancegenome.mati.interfaces;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.security.Authenticated;


/** Endpoints to mint AGR identifiers and get the last value of a counter  */
@Path("/identifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Identifier", description = "identifier Operations")
public interface IdentifierResourceRESTInterface {

	/** Endpoint for GET
	 * @param auth_header authorization token
	 * @param subdomain AGR subdomain
	 * @return an HTTP response with the last identifier minted for the given subdomain */
	@Authenticated
	@GET
	Response get(
		@NotNull(message = "Header does not have Authorization")
		@HeaderParam("Authorization") String auth_header,
		
		@NotNull(message = "Header does not have subdomain")
		@HeaderParam("subdomain") String subdomain
	);

	/** Endpoint for PUT: mints a new identifier for the given subdomain
	 * @param auth_header authorization token
	 * @param subdomain AGR subdomain
	 * @return an HTTP response with the identifier minted  */
	@Authenticated
	@PUT
	Response increment(
		@NotNull(message = "Header does not have Authorization")
		@HeaderParam("Authorization")
		String auth_header,
		
		@NotNull(message = "Header does not have subdomain")
		@HeaderParam("subdomain")
		String subdomain
	);

	/** Endpoint for POST: mints many identifiers for the given subdomain
	 * @param auth_header authorization token
	 * @param subdomain AGR subdomain
	 * @param value how many consecutive identifiers to mint
	 * @return an HTTP response with the identifiers minted  */
	@Authenticated
	@POST
	@Transactional
	Response increment(
		@NotNull(message = "Header does not have Authorization")
		@HeaderParam("Authorization")
		String auth_header,
		
		@NotNull(message = "Header does not have subdomain")
		@HeaderParam("subdomain")
		String subdomain,
		
		@NotNull(message = "Header does not have increment value")
		@HeaderParam("value")
		int value
	);
	
}