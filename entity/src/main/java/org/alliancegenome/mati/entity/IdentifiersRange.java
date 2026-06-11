package org.alliancegenome.mati.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/** AGR range of identifiers
 * <a href="https://github.com/alliance-genome/agr_curation_schema/blob/862cff5c081573e5e86ffe1eca83aa50d625f37e/model/schema/core.yaml#L414">IdentifierRange LinkML</a>
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentifiersRange {
    private Identifier first;
    private Identifier last;
}
