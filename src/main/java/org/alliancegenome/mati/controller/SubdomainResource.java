package org.alliancegenome.mati.controller;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.alliancegenome.mati.entity.SubdomainEntity;

/** Specifies the generation of the endpoint GET for subdomains
 *  with rest-data-panache */
@ResourceProperties(paged = false)
public interface SubdomainResource extends PanacheEntityResource<SubdomainEntity, Long> {
    /** Hides POST
     * @param id the id
     * @return HTTP response
     */
    @MethodProperties(exposed = false)
    Response add(Long id);

    /** Hides PUT
     * @param id the id
     * @return HTTP response
     */
    @MethodProperties(exposed = false)
    Response update(Long id);

    /** Hides DELETE
     * @param id the id
     * @return HTTP response
     */
    @MethodProperties(exposed = false)
    boolean delete(Long id);

    /** Hides count
     * @return HTTP response
     * */
    @MethodProperties(exposed = false)
    long count();

    /** Generates GET
     *
     * @param id the id
     * @return HTTP response
     */
    @MethodProperties(exposed = false)
    SubdomainEntity get(@PathParam("id") Long id);
}