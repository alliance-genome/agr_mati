package org.alliancegenome.mati.controller;

import io.quarkus.hibernate.orm.rest.data.panache.PanacheEntityResource;
import io.quarkus.rest.data.panache.MethodProperties;
import io.quarkus.rest.data.panache.ResourceProperties;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.alliancegenome.mati.entity.SubdomainEntity;


@ResourceProperties(paged = false)
public interface SubdomainResource extends PanacheEntityResource<SubdomainEntity, Long> {
    @MethodProperties(exposed = false)
    Response add(Long id);

    @MethodProperties(exposed = false)
    Response update(Long id);

    @MethodProperties(exposed = false)
    boolean delete(Long id);

    @MethodProperties(exposed = false)
    long count();

    @MethodProperties(exposed = false)
    SubdomainEntity get(@PathParam("id") Long id);
}