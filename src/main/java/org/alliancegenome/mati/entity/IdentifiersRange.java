package org.alliancegenome.mati.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdentifiersRange {
    private Identifier first;
    private Identifier last;
}
