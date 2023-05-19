package org.alliancegenome.mati.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/** AGR identifier
 * <a href="https://github.com/alliance-genome/agr_curation_schema/blob/862cff5c081573e5e86ffe1eca83aa50d625f37e/model/schema/core.yaml#L407">Identifier LinkML</a>
 * */
@Data
@AllArgsConstructor
public class Identifier {
    private Long counter;
    private String subdomain_code;
    private String subdomain_name;
    private String curie;
}
