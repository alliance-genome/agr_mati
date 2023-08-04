package org.alliancegenome.mati.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.alliancegenome.mati.configuration.ErrorResponse;
import org.alliancegenome.mati.entity.SubdomainEntity;
import org.alliancegenome.mati.interfaces.AdminRESTInterface;
import org.alliancegenome.mati.repository.SubdomainRepository;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;

import java.util.Map;

/**
 * Controller for administrative endpoints
 */
@RequestScoped
public class AdminResource implements AdminRESTInterface {

    @Inject
    SubdomainSequenceRepository subdomainSequenceRepository;
    @Inject
    SubdomainRepository subdomainRepository;

    public Response getCounters(String auth_header) {
        Map<String,Long> counters = subdomainSequenceRepository.getSubdomainCounters();
        if (counters.isEmpty()) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("admin.getCounters","No subdomains in database");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            Response.serverError().entity(errorResponse).build();
        }
        return Response.ok().entity(counters).build();
    }

    public Response setCounter(String auth_header, String subdomain, int value) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByName(subdomain);
        if (subdomainEntity == null) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("admin.setCounter","ID subdomain " + subdomain +" not found");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
        }
        boolean isDone = subdomainSequenceRepository.setSubdomainCounter(subdomainEntity, value);
        if (isDone) {
            return Response.ok().build();
        }
        else {
            return Response.notModified("Failure changing the value").build();
        }
    }

}
