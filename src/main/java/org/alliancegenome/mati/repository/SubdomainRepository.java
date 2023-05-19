package org.alliancegenome.mati.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.alliancegenome.mati.entity.SubdomainEntity;


/** Search of subdomains in the database */
@ApplicationScoped
public class SubdomainRepository implements PanacheRepositoryBase<SubdomainEntity, Long> {
    /** @param code  By code
     * @return the subdomain */
    public  SubdomainEntity findByCode(String code){
       return find("code", code).firstResult();
   }

    /** @param name By name
     * @return the subdomain */
    public  SubdomainEntity findByName(String name){
        return find("name", name).firstResult();
    }
}
