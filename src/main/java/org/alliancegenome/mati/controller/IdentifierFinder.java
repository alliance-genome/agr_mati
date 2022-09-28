package org.alliancegenome.mati.controller;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;

import org.alliancegenome.mati.configuration.ErrorResponse;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.interfaces.IdentifierFinderRESTInterface;
import org.alliancegenome.mati.repository.SubdomainRepository;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;

@RequestScoped
public class IdentifierFinder implements IdentifierFinderRESTInterface {

	@Inject
	SubdomainSequenceRepository subdomainSequenceRepository;
	@Inject
	SubdomainRepository subdomainRepository;

	public Response find(String auth_header, String identifier) {
		String trimmedID = identifier.trim();

		if (!trimmedID.startsWith("AGRKB:") || trimmedID.length() != 21) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","Wrong ID format -- AGRKB:123123456789012");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
		String subdomainCode = trimmedID.substring(6,9);
		String counter = trimmedID.substring(9);
		SubdomainEntity subdomainEntity = subdomainRepository.findByCode(subdomainCode);
		if (subdomainEntity == null) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","ID subdomain " + subdomainCode +" not found");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
		try {
			Long asNumber = Long.parseLong(counter);
			String status;
			if(subdomainSequenceRepository.getValue(subdomainEntity) >= asNumber) {
				status = "assigned";
			}
			else {
				status = "unassigned";
			}
			Map<String, String> map = new HashMap<>();
			map.put("status", status);
			return Response.ok().entity(map).build();
		} catch (NumberFormatException numberFormatException) {
			ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("finder.get","Not a number after prefix AGRKB:");
			ErrorResponse errorResponse = new ErrorResponse(errorMessage);
			return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
		}
	}
}
