package org.alliancegenome.mati.api.controller;

import org.alliancegenome.mati.api.configuration.ErrorResponse;
import org.alliancegenome.mati.api.repository.SubdomainRepository;
import org.alliancegenome.mati.api.repository.SubdomainSequenceRepository;
import org.alliancegenome.mati.entity.Identifier;
import org.alliancegenome.mati.entity.IdentifiersRange;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.interfaces.IdentifierResourceRESTInterface;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;

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

	private WebApplicationException badRequest(String path, String message) {
		ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage(path, message);
		ErrorResponse errorResponse = new ErrorResponse(errorMessage);
		return new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build());
	}

	private Identifier makeResultResponse(SubdomainEntity subdomainEntity, Long counter) {
		if (counter == -1L) {
			throw badRequest("identifier", "Failure retrieving/incrementing the counter");
		} else {
			Identifier identifier =  new Identifier(counter, subdomainEntity.getCode(), subdomainEntity.getName(),
				formatCounter(counter, subdomainEntity));
			return identifier;
		}
	}

	private IdentifiersRange makeResultResponse(SubdomainEntity subdomainEntity, Long firstValue, Long lastValue) {
		if ( firstValue==-1L || lastValue==-1L) {
			throw badRequest("identifier", "Failure incrementing the counter");
		} else {
			Identifier first = new Identifier(firstValue, subdomainEntity.getCode(), subdomainEntity.getName(),
				formatCounter(firstValue, subdomainEntity));
			Identifier last = new Identifier(lastValue, subdomainEntity.getCode(), subdomainEntity.getName(),
				formatCounter(lastValue, subdomainEntity));
			IdentifiersRange identifiersRange = new IdentifiersRange(first, last);
			return identifiersRange;
		}
	}


	public Identifier get(String auth_header, String subdomain) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			throw badRequest("finder.get", "ID subdomain " + subdomain + " not found");
		}
		Long result = subdomainSequenceRepository.getValue(subdomainEntity);
		return makeResultResponse(subdomainEntity, result);
	}

	public Identifier increment(String auth_header, String subdomain) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			throw badRequest("finder.get", "ID subdomain " + subdomain + " not found");
		}
		Long result = subdomainSequenceRepository.increment(subdomainEntity);
		return makeResultResponse(subdomainEntity, result);
	}

	public IdentifiersRange increment(String auth_header, String subdomain, int value) {
		SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
		if (subdomainEntity == null) {
			throw badRequest("identifier.post", "ID subdomain " + subdomain + " not found");
		}
		long firstValue = subdomainSequenceRepository.getValue(subdomainEntity) + 1;
		Long lastValue = subdomainSequenceRepository.increment(subdomainEntity, value);
		if (firstValue == -1L || lastValue == -1L) {
			throw badRequest("identifier.post", "failure incrementing subdomain " + subdomainEntity.getCode());
		}
		return makeResultResponse(subdomainEntity, firstValue, lastValue);
	}
}
