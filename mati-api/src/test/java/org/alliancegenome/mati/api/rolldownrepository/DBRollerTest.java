package org.alliancegenome.mati.api.rolldownrepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the SQL the rolldown runs against the target environment
 */
public class DBRollerTest {

    private static final Set<String> TARGET_SEQUENCES =
        Set.of("subdomain_agm_seq", "subdomain_allele_seq", "subdomain_variant_seq", "subdomain_cassette_seq");

    @Test
    public void countersOnlyMoveForward() {
        List<String> statements = DBRoller.prepareSETVALStatements(List.of("agm"), Map.of("agm", 801440L), TARGET_SEQUENCES);

        Assertions.assertEquals(
            List.of("SETVAL('subdomain_agm_seq',GREATEST(801440,(SELECT last_value FROM subdomain_agm_seq)))"),
            statements);
    }

    @Test
    public void skipsSubdomainsWithoutAPositiveSourceCounter() {
        List<String> statements = DBRoller.prepareSETVALStatements(
            List.of("allele", "cassette", "variant"),
            Map.of("allele", 3696244L, "cassette", 0L),
            TARGET_SEQUENCES);

        Assertions.assertEquals(
            List.of("SETVAL('subdomain_allele_seq',GREATEST(3696244,(SELECT last_value FROM subdomain_allele_seq)))"),
            statements);
    }

    @Test
    public void skipsSubdomainsTheTargetHasNoSequenceFor() {
        List<String> statements = DBRoller.prepareSETVALStatements(
            List.of("agm", "transgenic_tool"),
            Map.of("agm", 801440L, "transgenic_tool", 100L),
            TARGET_SEQUENCES);

        Assertions.assertEquals(
            List.of("SETVAL('subdomain_agm_seq',GREATEST(801440,(SELECT last_value FROM subdomain_agm_seq)))"),
            statements);
    }
}
