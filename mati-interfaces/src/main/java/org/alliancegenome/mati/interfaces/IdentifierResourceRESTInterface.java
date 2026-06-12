package org.alliancegenome.mati.interfaces;

import org.alliancegenome.mati.entity.Identifier;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.security.Authenticated;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


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
	Identifier get(
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
	Identifier increment(
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
	IdentifiersRange increment(
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