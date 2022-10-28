package org.alliancegenome.mati.interfaces;

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

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.quarkus.security.Authenticated;

@Path("/identifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Identifier", description = "identifier Operations")
public interface IdentifierResourceRESTInterface {

	@Authenticated
	@GET
	Response get(
		@NotNull(message = "Header does not have Authorization")
		@HeaderParam("Authorization") String auth_header,
		
		@NotNull(message = "Header does not have subdomain")
		@HeaderParam("subdomain") String subdomain
	);

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