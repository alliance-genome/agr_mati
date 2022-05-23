package org.alliancegenome.mati.repository;

import lombok.NoArgsConstructor;
import org.alliancegenome.mati.entity.SubdomainEntity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@NoArgsConstructor
@ApplicationScoped
public class SubdomainSequenceRepository {
    private static final int MAX_NUMBER_IDS = 10000000;
    @Inject
    SubdomainRepository subdomainRepository;
    @Inject
    EntityManager entityManager;

    public Long getValue(String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByCode(subdomain);
        if (subdomainEntity == null)
            return -1L;
        String sequenceName = "subdomain_" + subdomainEntity.getCode() + "_seq";
        String sql = "SELECT COALESCE( (SELECT last_value FROM pg_sequences WHERE sequencename = :sequenceName)," +
                                      "(SELECT min_value  FROM pg_sequences WHERE sequencename = :sequenceName))";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName",  sequenceName);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        }
        catch (Exception e) {
            return -1L;
        }
    }

    public Long increment(String subdomain) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByCode(subdomain);
        if (subdomainEntity == null)
            return -1L;
        String sequenceName = "subdomain_" + subdomainEntity.getCode() + "_seq";
        String sql = "SELECT NEXTVAL(:sequenceName)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName", sequenceName);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        } catch (Exception e) {
            return -1L;
        }
    }

    public Long increment(String subdomain, int value) {
        SubdomainEntity subdomainEntity = subdomainRepository.findByCode(subdomain);
        if (subdomainEntity == null)
            return -1L;
        if (value < 1  || value > MAX_NUMBER_IDS)
            return -1L;
        String sequenceName = "subdomain_" + subdomainEntity.getCode() + "_seq";
        String sql = "SELECT SETVAL(:sequenceName, " +
            "(SELECT :value + (SELECT COALESCE( (SELECT last_value FROM pg_sequences WHERE sequencename = :sequenceName)," +
                                               "(SELECT min_value  FROM pg_sequences WHERE sequencename = :sequenceName)))))";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName", sequenceName);
        query.setParameter("value", value);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        } catch (Exception e) {
            return -1L;
        }
    }
}
