package org.alliancegenome.mati.repository;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import lombok.NoArgsConstructor;
import org.alliancegenome.mati.entity.SubdomainEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** Manipulates the counters, represented by PostgresSQL sequences, in the database */
@NoArgsConstructor
@ApplicationScoped
public class SubdomainSequenceRepository {
    private static final int MAX_NUMBER_IDS = 10000000;
    @Inject
    EntityManager entityManager;

    /**
     * Get the last minted identifier
     * @param subdomainEntity subdomain given
     * @return the value of the counter
     */
    public Long getValue(SubdomainEntity subdomainEntity) {
        String sequenceName = "subdomain_" + subdomainEntity.getName() + "_seq";
        String sql = "SELECT COALESCE( (SELECT last_value FROM pg_sequences WHERE sequencename = :sequenceName)," +
                                      "0)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName",  sequenceName);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        }
        catch (Exception e) {
            return -1L;
        }
    }

    /**
     * increment the subdomain counter by 1
     * @param subdomainEntity subdomain given
     * @return the value of the counter
     */
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

    /**
     * increment the subdomain counter by the given value
     * @param subdomainEntity subdomain given
     * @param value the given value
     * @return the value of the counter
     */
    public Long increment(SubdomainEntity subdomainEntity, int value) {
        if (value < 1  || value > MAX_NUMBER_IDS)
            return -1L;
        String sequenceName = "subdomain_" + subdomainEntity.getName() + "_seq";
        String sql = "SELECT SETVAL(:sequenceName, " +
            "(SELECT :value + (SELECT COALESCE( (SELECT last_value FROM pg_sequences WHERE sequencename = :sequenceName)," +
                                               "0))))";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName", sequenceName);
        query.setParameter("value", value);
        try {
            return Long.parseLong(query.getSingleResult().toString());
        } catch (Exception e) {
            return -1L;
        }
    }

    /**
     * Sets  the subdomain counter to a value
     * @param subdomain the given subdomain
     * @param value the value to assign
     * @return a boolean indicating success
     */
    public boolean setSubdomainCounter(SubdomainEntity subdomain, int value) {
        if (value <= 0 )
            return false;
        String sequenceName = "subdomain_" + subdomain.getName() + "_seq";
        String sql = "SELECT SETVAL(:sequenceName, :value)";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sequenceName", sequenceName);
        query.setParameter("value", value);
        try {
            query.getSingleResult();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns the values of all the counters (PostgreSQL sequences)
     * @return a dictionary {sequence -> value}
     */
    public Map<String,Long> getSubdomainCounters() {
        Map<String,Long> result = new HashMap<>();
        // This query gets all the counter values
        String sql = "SELECT sequencename, last_value FROM pg_sequences WHERE sequencename LIKE 'subdomain_%_seq'";
        Query query = entityManager.createNativeQuery(sql, Tuple.class);
        try {
            List<Tuple> tuples = query.getResultList();
            for (Tuple tuple : tuples) {
                String pg_sequence = tuple.get("sequencename").toString();
                Object counter = tuple.get("last_value");
                // the subdomain table has a key with autoincrement that we ignore here
                if (!pg_sequence.equals("subdomain_id_seq")) {
                    int start = "subdomain_".length();
                    int end = pg_sequence.length() - "_seq".length();
                    String subdomain = pg_sequence.substring(start, end);
                    result.put(subdomain, counter==null ? 0 : (Long) counter);
                }
            }
        } catch (Exception e) {
            return new HashMap<>();
        }
        return result;
    }
}
