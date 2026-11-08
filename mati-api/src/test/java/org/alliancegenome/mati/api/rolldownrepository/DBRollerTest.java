package org.alliancegenome.mati.api.rolldownrepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * Tests the SQL the rolldown runs against the target environment
 */
public class DBRollerTest {

    @Test
    public void countersOnlyMoveForward() {
        List<String> statements = DBRoller.prepareSETVALStatements(List.of("agm"), Map.of("agm", 801440L));

        Assertions.assertEquals(
            List.of("SETVAL('subdomain_agm_seq',GREATEST(801440,(SELECT last_value FROM subdomain_agm_seq)))"),
            statements);
    }

    @Test
    public void skipsSubdomainsWithoutAPositiveSourceCounter() {
        List<String> statements = DBRoller.prepareSETVALStatements(
            List.of("allele", "cassette", "variant"),
            Map.of("allele", 3696244L, "cassette", 0L));

        Assertions.assertEquals(
            List.of("SETVAL('subdomain_allele_seq',GREATEST(3696244,(SELECT last_value FROM subdomain_allele_seq)))"),
            statements);
    }
}
