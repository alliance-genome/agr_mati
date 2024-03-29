package org.alliancegenome.mati.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.alliancegenome.mati.configuration.ErrorResponse;
import org.alliancegenome.mati.entity.Identifier;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.interfaces.IdentifierResourceRESTInterface;
import org.alliancegenome.mati.repository.SubdomainRepository;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;

/** Endpoints to mint AGR identifiers and get the last value of a counter  */
@AllArgsConstructor
@Path("/identifier")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Identifier", description = "identifier Operations")
public class IdentifierResource implements IdentifierResourceRESTInterface {
	@Inject
	SubdomainSequenceRepository subdomainSequenceRepository;
	@Inject
	SubdomainRepository subdomainRepository;

	private String formatCounter(Long counter, SubdomainEntity subdomainEntity) {
		return "AGRKB:" + subdomainEntity.getCode() +
				String.format("%0" + 12 + "d", counter);
	}

	private Response makeResultResponse(SubdomainEntity subdomainEntity, Long counter) {
		if (counter == -1L) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier","Failure retrieving/incrementing the counter");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		} else {
			Identifier identifier =  new Identifier(counter, subdomainEntity.getCode(), subdomainEntity.getName(),
				formatCounter(counter, subdomainEntity));
			return Response.ok().entity(identifier).build();
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


	public Response get(String auth_header, String subdomain) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","ID subdomain " + subdomain +" not found");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
		Long result = subdomainSequenceRepository.getValue(subdomainEntity);
		return makeResultResponse(subdomainEntity, result);
	}

	public Response increment(String auth_header, String subdomain) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","ID subdomain " + subdomain +" not found");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
		Long result = subdomainSequenceRepository.increment(subdomainEntity);
		return makeResultResponse(subdomainEntity, result);
	}

	public Response increment(String auth_header, String subdomain, int value) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier.post","ID subdomain " + subdomain +" not found");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
		long firstValue = subdomainSequenceRepository.getValue(subdomainEntity) + 1;
		Long lastValue = subdomainSequenceRepository.increment(subdomainEntity, value);
		if (firstValue == -1L || lastValue == -1L) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("identifier.post","failure incrementing subdomain " + subdomainEntity.getCode());
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();

		}
		return makeResultResponse(subdomainEntity, firstValue, lastValue);
	}
}
