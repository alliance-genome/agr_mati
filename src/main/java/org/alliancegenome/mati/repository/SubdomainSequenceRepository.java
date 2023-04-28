package org.alliancegenome.mati.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.NoArgsConstructor;
import org.alliancegenome.mati.entity.SubdomainEntity;

import jakarta.inject.Inject;


@NoArgsConstructor
@ApplicationScoped
public class SubdomainSequenceRepository {
    private static final int MAX_NUMBER_IDS = 10000000;
    @Inject
    EntityManager entityManager;

    public Long getValue(SubdomainEntity subdomainEntity) {
        String sequenceName = "subdomain_" + subdomainEntity.getName() + "_seq";
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

    public Long increment(SubdomainEntity subdomainEntity) {
        String sequenceName = "subdomain_" + subdomainEntity.getName() + "_seq";
        String sql = "SELECT NEXTVAL(:sequenceName)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName", sequenceName);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        } catch (Exception e) {
            return -1L;
        }
    }

    public Long increment(SubdomainEntity subdomainEntity, int value) {
        if (value < 1  || value > MAX_NUMBER_IDS)
            return -1L;
        String sequenceName = "subdomain_" + subdomainEntity.getName() + "_seq";
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
