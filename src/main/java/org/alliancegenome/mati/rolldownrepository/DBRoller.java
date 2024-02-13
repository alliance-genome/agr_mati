package org.alliancegenome.mati.rolldownrepository;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rolls-down the MaTI database from one environment to other
 * prod -> beta
 * beta -> alpha
 */
@RequestScoped
public class DBRoller {

    @PersistenceUnit("rolldown")
    @Inject
    EntityManager entityManager;

    /**
     *  Creates the SQL sentences for setting the PostgreSQL sequences values
     * @param subdomains the list of subdomains
     * @param counters a dictionary mapping subdomains to values (e.g. subdomain_person -> 120 )
     * @return a list of SQL statements. For example:
     * [ SETVAL('subdomain_name1_seq',10) ... SETVAL('subdomain_nameN_seq',N) ]
     */
    private List<String> prepareSETVALStatements(List<String> subdomains, Map<String,Long> counters) {
        List<String> setVALStatements = new ArrayList<>();
        for (String subdomain : subdomains) {
            if (counters.containsKey(subdomain)) {
                Long counter = counters.get(subdomain);
                if (counter > 0) {
                    setVALStatements.add("SETVAL('subdomain_" + subdomain + "_seq'," + counter + ")");
                }
            }
        }
        return setVALStatements;
    }

    /**
     * Rolls down the values from one environment to other
     * @param subdomains the list of subdomains
     * @param counters a dictionary mapping subdomains to values (e.g. subdomain_reference -> 20 )
     * @return a success/failure value
     */
    public boolean setSubdomainCounters(List<String> subdomains, Map<String,Long> counters) {
        List<String> setStatements = prepareSETVALStatements(subdomains, counters);
        if (!setStatements.isEmpty()) {
            String body = String.join(",", setStatements);
            String sql = "SELECT " + body;
            Query query = entityManager.createNativeQuery(sql);
            try {
                int num_queries = query.getResultList().size();
                if (num_queries == setStatements.size()) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
