package org.alliancegenome.mati.api.rolldownrepository;

import io.quarkus.hibernate.orm.PersistenceUnit;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * Each counter only moves forward: a target that already minted past the source keeps its own value,
     * so a rolldown never hands out an identifier the target environment has already used.
     * A subdomain whose sequence the target doesn't have yet (it runs an older MaTI release) is skipped,
     * so it doesn't stop the other counters from rolling down.
     * @param subdomains the list of subdomains
     * @param counters a dictionary mapping subdomains to values (e.g. subdomain_person -> 120 )
     * @param targetSequences the names of the subdomain sequences in the target database
     * @return a list of SQL statements. For example:
     * [ SETVAL('subdomain_name1_seq',GREATEST(10,(SELECT last_value FROM subdomain_name1_seq))) ... ]
     */
    static List<String> prepareSETVALStatements(List<String> subdomains, Map<String,Long> counters, Set<String> targetSequences) {
        List<String> setVALStatements = new ArrayList<>();
        for (String subdomain : subdomains) {
            if (counters.containsKey(subdomain)) {
                Long counter = counters.get(subdomain);
                String sequenceName = "subdomain_" + subdomain + "_seq";
                if (counter > 0 && targetSequences.contains(sequenceName)) {
                    setVALStatements.add("SETVAL('" + sequenceName + "',GREATEST(" + counter + ",(SELECT last_value FROM " + sequenceName + ")))");
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
        try {
            List<String> setStatements = prepareSETVALStatements(subdomains, counters, getTargetSequences());
            if (!setStatements.isEmpty()) {
                // a single SELECT of all the SETVALs: it returns one row, and fails as a whole if any of them fails
                String sql = "SELECT " + String.join(",", setStatements);
                entityManager.createNativeQuery(sql).getResultList();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return the names of the subdomain sequences in the target database
     */
    @SuppressWarnings("unchecked")
    private Set<String> getTargetSequences() {
        Query query = entityManager.createNativeQuery(
            "SELECT CAST(sequencename AS text) FROM pg_sequences WHERE sequencename LIKE 'subdomain\\_%'");
        return new HashSet<>((List<String>) query.getResultList());
    }
}
