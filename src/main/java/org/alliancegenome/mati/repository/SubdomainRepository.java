package org.alliancegenome.mati.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import org.alliancegenome.mati.entity.SubdomainEntity;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SubdomainRepository implements PanacheRepositoryBase<SubdomainEntity, Long> {
    public SubdomainEntity findByCode(String code){
       return find("code", code).firstResult();
   }
}
