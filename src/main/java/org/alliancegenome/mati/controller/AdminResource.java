package org.alliancegenome.mati.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.alliancegenome.mati.configuration.ErrorResponse;
import org.alliancegenome.mati.interfaces.AdminRESTInterface;
import org.alliancegenome.mati.repository.SubdomainSequenceRepository;
import org.alliancegenome.mati.rolldownrepository.DBRoller;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

/**
 * Controller for:
 *  getting the current values of the counters (subdomains)
 *  and rolling down the MaTI database
 */
@RequestScoped
public class AdminResource implements AdminRESTInterface {

    @Inject
    SubdomainSequenceRepository subdomainSequenceRepository;

    @Inject
    DBRoller dbRoller;

    private static final  String NET = System.getenv("NET");;

    public Response getCounters() {
        Map<String,Long> counters = subdomainSequenceRepository.getSubdomainCounters();
        if (counters.isEmpty()) {
            ErrorResponse.ErrorMessage errorMessage = new ErrorResponse.ErrorMessage("admin.getCounters","No subdomains in database");
            ErrorResponse errorResponse = new ErrorResponse(errorMessage);
            Response.serverError().entity(errorResponse).build();
        }
        return Response.ok().entity(counters).build();
    }

    /**
     * rolls down the status of Mati counters for the curation application
     * @param auth_header with authorization
     * @return a success/failure HTTP response
     */
    @Transactional
    public Response rolldown_for_curation(String auth_header) {
        if (NET.equals("alpha")) {
            return Response.ok().build();
        }
        List<String> subdomains = List.of("disease_annotation");
        return rolldown(subdomains);
    }

    /**
     * rolls down the status of Mati counters from one environment to another
     * prod -> beta
     * beta -> alpha
     * @param subdomains the list of subdomains to roll down
     * @return a success/failure HTTP response
     */
    private Response rolldown(List<String> subdomains) {
        Map<String,Long> counters = subdomainSequenceRepository.getSubdomainCounters();
        if (dbRoller.setSubdomainCounters(subdomains, counters)) {
            return Response.ok().build();
        }
        else {
            return Response.notModified("Failure changing the values").build();
        }
    }
}
