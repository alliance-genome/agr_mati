package org.alliancegenome.mati.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.mati.entity.SubdomainEntity;


@ApplicationScoped
public class SubdomainRepository implements PanacheRepositoryBase<SubdomainEntity, Long> {
    public  SubdomainEntity findByCode(String code){
       return find("code", code).firstResult();
   }

    public  SubdomainEntity findByName(String name){
        return find("name", name).firstResult();
    }
}
